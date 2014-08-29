package com.base22.carbon.security.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.RDFGenericRequest;
import com.base22.carbon.models.RDFGenericRequestFactory;
import com.base22.carbon.security.models.Agent;
import com.base22.carbon.security.models.ApplicationRole;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ApplicationRolePOSTRequestHandler extends AbstractApplicationRoleAPIRequestHandler {

	public ResponseEntity<Object> handleRequest(String appIdentifier, String targetAppRoleUUID, Model requestModel, HttpServletRequest request,
			HttpServletResponse response) throws CarbonException {

		Resource requestResource = getRequestModelMainResource(requestModel);
		RDFGenericRequest rdfGenericRequest = getRDFGenericRequest(requestResource);

		// TODO: Take into account the application we are in
		ApplicationRole targetAppRole = getTargetApplicationRole(targetAppRoleUUID);

		if ( ! targetAppRoleExists(targetAppRole) ) {
			return handleNonExistentAppRole(targetAppRoleUUID, request, response);
		}

		List<Agent> targetAgents = getTargetAgents(rdfGenericRequest);

		// TODO: Get the emails that were not attached to an existing agent

		addTargetAgentsToTargetAppRole(targetAppRole, targetAgents);

		// TODO: Construct a more descriptive response (Were all the agents added?, etc.)
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	private RDFGenericRequest getRDFGenericRequest(Resource requestResource) throws CarbonException {
		RDFGenericRequest rdfGenericRequest = null;
		RDFGenericRequestFactory factory = new RDFGenericRequestFactory();
		try {
			rdfGenericRequest = factory.create(requestResource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return rdfGenericRequest;
	}

	private List<Agent> getTargetAgents(RDFGenericRequest rdfGenericRequest) throws CarbonException {
		List<Agent> targetAgents = new ArrayList<Agent>();
		String[] agentEmails = rdfGenericRequest.getStringProperties(Agent.Properties.EMAIL.getProperty());

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
