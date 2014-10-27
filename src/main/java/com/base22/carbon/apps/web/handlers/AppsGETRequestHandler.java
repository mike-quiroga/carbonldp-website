package com.base22.carbon.apps.web.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationRDF;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerClass;
import com.base22.carbon.ldp.models.ContainerFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class AppsGETRequestHandler extends AbstractAppRequestHandler {
	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws CarbonException {
		List<Application> targetApplications = getTargetApplications();
		List<ApplicationRDF> targetRDFApplications = getRDFTargetApplications(targetApplications);
		Container appsContainer = getAppsContainer(targetRDFApplications);

		return new ResponseEntity<Object>(appsContainer, HttpStatus.OK);
	}

	private List<Application> getTargetApplications() throws CarbonException {
		List<Application> targetApplications = null;
		try {
			targetApplications = securedApplicationDAO.getApplications();
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetApplications;
	}

	private List<ApplicationRDF> getRDFTargetApplications(List<Application> targetApplications) {
		List<ApplicationRDF> targetRDFApplications = new ArrayList<ApplicationRDF>();

		for (Application app : targetApplications) {
			targetRDFApplications.add(app.createRDFRepresentation());
		}

		return targetRDFApplications;
	}

	private Model combineRDFTargetApplications(List<ApplicationRDF> targetRDFApplications) {
		Model combinedModel = ModelFactory.createDefaultModel();
		for (ApplicationRDF rdfApplication : targetRDFApplications) {
			combinedModel.add(rdfApplication.getResource().getModel());
		}
		return combinedModel;
	}

	private Container getAppsContainer(List<ApplicationRDF> targetRDFApplications) throws CarbonException {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(Carbon.URL).append("/apps/");

		Container appsContainer = null;
		ContainerFactory factory = new ContainerFactory();
		try {
			appsContainer = factory.createBasicContainer(uriBuilder.toString());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		for (ApplicationRDF targetRDFApplication : targetRDFApplications) {
			appsContainer.addProperty(ContainerClass.Properties.DEFAULT_HAS_MEMBER_RELATION.getProperty(), targetRDFApplication.getResource());
		}

		return appsContainer;
	}
}