package com.base22.carbon.apps.roles.web.handlers;

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
import com.base22.carbon.agents.Agent;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.models.EmptyResponse;
import com.base22.carbon.models.GenericRequestRDF;
import com.base22.carbon.models.GenericRequestRDFFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RolePOSTRequestHandler extends AbstractRoleRequestHandler {

	public ResponseEntity<Object> handleRequest(String appSlug, String targetAppRoleSlug, Model requestModel, HttpServletRequest request,
			HttpServletResponse response) throws CarbonException {

		Resource requestResource = getRequestModelMainResource(requestModel);
		GenericRequestRDF rdfGenericRequest = getRDFGenericRequest(requestResource);

		// TODO: Take into account the application we are in
		ApplicationRole targetAppRole = getTargetApplicationRole(targetAppRoleSlug, appSlug);

		if ( ! targetAppRoleExists(targetAppRole) ) {
			return handleNonExistentAppRole(targetAppRoleSlug, request, response);
		}

		List<Agent> targetAgents = getTargetAgents(rdfGenericRequest);

		// TODO: Get the emails that were not attached to an existing agent

		addTargetAgentsToTargetAppRole(targetAppRole, targetAgents);

		// TODO: Construct a more descriptive response (Were all the agents added?, etc.)
		return new ResponseEntity<Object>(new EmptyResponse(), HttpStatus.OK);
	}

	private GenericRequestRDF getRDFGenericRequest(Resource requestResource) throws CarbonException {
		GenericRequestRDF rdfGenericRequest = null;
		GenericRequestRDFFactory factory = new GenericRequestRDFFactory();
		try {
			rdfGenericRequest = factory.create(requestResource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return rdfGenericRequest;
	}

	private List<Agent> getTargetAgents(GenericRequestRDF rdfGenericRequest) throws CarbonException {
		List<Agent> targetAgents = new ArrayList<Agent>();
		String[] agentEmails = rdfGenericRequest.getStrings(Agent.Properties.EMAIL.getProperty());

		try {
			targetAgents = unsecuredAgentDetailsDAO.getByEmails(agentEmails);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		return targetAgents;
	}

	private void addTargetAgentsToTargetAppRole(ApplicationRole targetAppRole, List<Agent> targetAgents) throws CarbonException {
		List<ApplicationRole> parentAppRoles = null;
		try {
			parentAppRoles = unsecuredApplicationRoleDAO.getAllParentsOfApplicationRole(targetAppRole.getUuid());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		List<ApplicationRole> childrenAppRoles = null;
		try {
			childrenAppRoles = unsecuredApplicationRoleDAO.getAllChildrenOfApplicationRole(targetAppRole.getUuid());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		for (Agent targetAgent : targetAgents) {
			List<ApplicationRole> agentAppRoles = null;
			try {
				agentAppRoles = unsecuredApplicationRoleDAO.getApplicationRolesOfAgent(targetAgent.getUuid());
			} catch (CarbonException e) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				throw e;
			}

			if ( agentAppRoles.contains(targetAppRole) ) {
				// The agent already has the applicationRole
				break;
			}

			boolean alreadyHasParent = false;
			for (ApplicationRole parentAppRole : parentAppRoles) {
				if ( agentAppRoles.contains(parentAppRole) ) {
					alreadyHasParent = true;
					break;
				}
			}

			if ( alreadyHasParent ) {
				// TODO: Notify this in the response
				// TODO: Decide, fail the request?
				break;
			}

			List<ApplicationRole> agentChildRoles = new ArrayList<ApplicationRole>();
			for (ApplicationRole childrenAppRole : childrenAppRoles) {
				if ( agentAppRoles.contains(childrenAppRole) ) {
					agentChildRoles.add(childrenAppRole);
				}
			}

			if ( ! agentChildRoles.isEmpty() ) {
				// TODO: Instead of skipping, add the agent and then remove the childroles
				break;
			}

			try {
				securedApplicationRoleDAO.addAgentToApplicationRole(targetAppRole, targetAgent.getUuid());
			} catch (CarbonException e) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				throw e;
			}
		}
	}
}
