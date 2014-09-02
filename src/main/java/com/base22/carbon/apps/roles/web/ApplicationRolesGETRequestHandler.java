package com.base22.carbon.apps.roles.web;

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
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.apps.roles.RDFApplicationRole;
import com.base22.carbon.apps.web.AbstractApplicationAPIRequestHandler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ApplicationRolesGETRequestHandler extends AbstractApplicationAPIRequestHandler {
	public ResponseEntity<Object> handleRequest(String appIdentifier, HttpServletRequest request, HttpServletResponse response) throws CarbonException {
		Application application = securedApplicationDAO.findByIdentifier(appIdentifier);

		List<ApplicationRole> targetAppRoles = getTargetAppRoles(application);
		List<RDFApplicationRole> targetRDFAppRoles = getTargetRDFAppRoles(targetAppRoles);

		Model combinedModel = combineTargetRDFAppRoles(targetRDFAppRoles);

		return new ResponseEntity<Object>(combinedModel, HttpStatus.OK);
	}

	private List<ApplicationRole> getTargetAppRoles(Application application) throws CarbonException {
		List<ApplicationRole> targetAppRoles = null;
		try {
			targetAppRoles = securedApplicationRoleDAO.getApplicationRolesOfApplication(application.getUuid());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetAppRoles;
	}

	private List<RDFApplicationRole> getTargetRDFAppRoles(List<ApplicationRole> targetAppRoles) {
		List<RDFApplicationRole> targetRDFAppRoles = new ArrayList<RDFApplicationRole>();

		for (ApplicationRole role : targetAppRoles) {
			targetRDFAppRoles.add(role.createRDFRepresentation());
		}

		return targetRDFAppRoles;
	}

	private Model combineTargetRDFAppRoles(List<RDFApplicationRole> targetRDFAppRoles) {
		Model combinedModel = ModelFactory.createDefaultModel();
		for (RDFApplicationRole rdfAppRole : targetRDFAppRoles) {
			combinedModel.add(rdfAppRole.getResource().getModel());
		}
		return combinedModel;
	}
}
