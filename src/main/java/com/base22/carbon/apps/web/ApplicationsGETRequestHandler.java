package com.base22.carbon.apps.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.RDFApplication;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ApplicationsGETRequestHandler extends AbstractApplicationAPIRequestHandler {
	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws CarbonException {
		List<Application> targetApplications = getTargetApplications();
		List<RDFApplication> targetRDFApplications = getRDFTargetApplications(targetApplications);
		Model combinedApplicationsModel = combineRDFTargetApplications(targetRDFApplications);

		return new ResponseEntity<Object>(combinedApplicationsModel, HttpStatus.OK);
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

	private List<RDFApplication> getRDFTargetApplications(List<Application> targetApplications) {
		List<RDFApplication> targetRDFApplications = new ArrayList<RDFApplication>();

		for (Application app : targetApplications) {
			targetRDFApplications.add(app.createRDFRepresentation());
		}

		return targetRDFApplications;
	}

	private Model combineRDFTargetApplications(List<RDFApplication> targetRDFApplications) {
		Model combinedModel = ModelFactory.createDefaultModel();
		for (RDFApplication rdfApplication : targetRDFApplications) {
			combinedModel.add(rdfApplication.getResource().getModel());
		}
		return combinedModel;
	}
}
