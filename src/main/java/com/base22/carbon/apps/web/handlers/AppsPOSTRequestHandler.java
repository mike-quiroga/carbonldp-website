package com.base22.carbon.apps.web.handlers;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationClass.Properties;
import com.base22.carbon.apps.ApplicationRDF;
import com.base22.carbon.apps.ApplicationRDFFactory;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.authorization.AuthorizationUtil;
import com.base22.carbon.authorization.acl.CarbonACLPermissionFactory.CarbonPermission;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class AppsPOSTRequestHandler extends AbstractAppRequestHandler {

	public ResponseEntity<Object> handleRequest(Model requestModel, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		Resource requestResource = getRequestModelMainResource(requestModel);
		ApplicationRDF requestRDFApplication = getRequestRDFApplication(requestResource);

		validateRequestRDFApplication(requestRDFApplication);

		if ( slugWasProvided(requestRDFApplication) ) {

			sluggifyProvidedSlug(requestRDFApplication);

			if ( slugIsAlreadyInUse(requestRDFApplication) ) {
				return handleSlugAlreadyRegistred(requestRDFApplication, request, response);
			}
		}

		Application requestApplication = getRequestApplication(requestRDFApplication);

		UUID datasetUUID = createApplicationDataset();
		setRequestApplicationDataset(datasetUUID, requestApplication);

		requestApplication = createApplication(requestApplication);

		ApplicationRole rootAppRole = createRootAppRole(requestApplication);
		Sid rootAppRoleSid = registerRootAppRole(rootAppRole);
		addAgentToRootAppRole(rootAppRole);

		Container rootContainer = createRootContainer(requestApplication);
		URIObject rootContainerUO = saveRootContainer(rootContainer, requestApplication);

		addDefaultPermissionsToApplication(rootAppRoleSid, requestApplication);
		addDefaultPermissionsToRootContainer(rootAppRoleSid, rootContainerUO);
		addDefaultPermissionsToRootAppRole(rootAppRoleSid, rootAppRole);

		return new ResponseEntity<Object>(requestApplication.createRDFRepresentation(), HttpStatus.CREATED);

	}

	private void addAgentToRootAppRole(ApplicationRole rootAppRole) throws CarbonException {
		UUID agentUUID = null;
		try {
			agentUUID = AuthorizationUtil.getAgentUUIDFromContext();
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		try {
			unsecuredApplicationRoleDAO.addAgentToApplicationRole(rootAppRole, agentUUID);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private ResponseEntity<Object> handleSlugAlreadyRegistred(ApplicationRDF requestRDFApplication, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The slug provided is already in use.";
		String debugMessage = MessageFormat.format("The slug provided is already in use.", requestRDFApplication.getSlug());

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleSlugAlreadyRegistred() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.CONFLICT);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private ApplicationRDF getRequestRDFApplication(Resource requestResource) throws CarbonException {
		ApplicationRDF requestRDFApplication = null;
		ApplicationRDFFactory factory = new ApplicationRDFFactory();
		try {
			requestRDFApplication = factory.create(requestResource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return requestRDFApplication;
	}

	// TODO: Refactor (make it smaller)
	private void validateRequestRDFApplication(ApplicationRDF requestRDFApplication) throws CarbonException {
		StringBuilder bodyIssueBuilder = null;

		if ( requestRDFApplication.getUUID() != null ) {
			bodyIssueBuilder = new StringBuilder();

			bodyIssueBuilder.append(Properties.UUID.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > This property is set by the server. It cannot be specified upon creation.");
		}

		if ( requestRDFApplication.getSlug() != null ) {
			if ( requestRDFApplication.getSlug().trim().length() == 0 ) {
				if ( bodyIssueBuilder == null ) {
					bodyIssueBuilder = new StringBuilder();
				} else {
					bodyIssueBuilder.append("\n");
				}

				bodyIssueBuilder.append(Properties.SLUG.getPrefixedURI().getSlug());
				bodyIssueBuilder.append(" > Required.");
			}
		}

		if ( requestRDFApplication.getName() == null || requestRDFApplication.getName().trim().length() == 0 ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.NAME.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Required.");
		}

		if ( requestRDFApplication.getMasterKey() != null ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.MASTER_KEY.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > This property is set by the server. It cannot be specified upon creation.");
		}

		if ( bodyIssueBuilder != null ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The properties of the Application sent are not valid.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateRequestRDFApplication() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, bodyIssueBuilder.toString());

			throw new CarbonException(errorObject);
		}

	}

	private boolean slugWasProvided(ApplicationRDF requestRDFApplication) {
		return requestRDFApplication.getSlug() != null;
	}

	private boolean slugIsAlreadyInUse(ApplicationRDF requestRDFApplication) throws CarbonException {
		try {
			return securedApplicationDAO.applicationExistsWithSlug(requestRDFApplication.getSlug());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void sluggifyProvidedSlug(ApplicationRDF requestRDFApplication) {
		requestRDFApplication.setSlug(HTTPUtil.createSlug(requestRDFApplication.getSlug()));
	}

	private Application getRequestApplication(ApplicationRDF requestRDFApplication) throws CarbonException {
		Application requestApplication = new Application();
		try {
			requestApplication.recoverFromLDPR(requestRDFApplication);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return requestApplication;
	}

	private UUID createApplicationDataset() throws CarbonException {
		// Create the application's dataset
		UUID datasetUUID = UUID.randomUUID();
		String datasetName = datasetUUID.toString();

		try {
			repositoryService.createDataset(datasetName);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		return datasetUUID;
	}

	private void setRequestApplicationDataset(UUID datasetUUID, Application requestApplication) throws CarbonException {
		requestApplication.setDatasetUuid(datasetUUID);
	}

	private Application createApplication(Application requestApplication) throws CarbonException {
		try {
			requestApplication = securedApplicationDAO.createApplication(requestApplication);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return requestApplication;
	}

	private ApplicationRole createRootAppRole(Application requestApplication) throws CarbonException {
		ApplicationRole appRole = new ApplicationRole();
		appRole.setName(configurationService.getDefaultRootApplicationRoleName());
		appRole.setSlug(configurationService.getDefaultRootApplicationRoleSlug());
		try {
			appRole = unsecuredApplicationRoleDAO.createRootApplicationRole(requestApplication, appRole);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return appRole;
	}

	private Sid registerRootAppRole(ApplicationRole rootAppRole) {
		return new GrantedAuthoritySid(rootAppRole);
	}

	private Container createRootContainer(Application requestApplication) throws CarbonException {
		StringBuilder uriBuilder = new StringBuilder();

		//@formatter:off
		uriBuilder
			.append(configurationService.getServerURL())
			.append("/apps/")
			.append(requestApplication.getIdentifier())
			.append("/")
		;
		
		ContainerFactory containerFactory = new ContainerFactory();
		Container rootContainer = null;
		try {
			rootContainer = containerFactory.createBasicContainer(uriBuilder.toString());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;		
		}

		return rootContainer;

	}

	private URIObject saveRootContainer(Container rootContainer, Application requestApplication) throws CarbonException {
		URIObject rootContainerUO = null;
		try {
			rootContainerUO = ldpService.createRootContainer(rootContainer, requestApplication.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;	
		}
		return rootContainerUO;
	}
	
	private void addDefaultPermissionsToApplication(Sid rootAppRoleSid, Application requestApplication) throws CarbonException {
		try {
			//@formatter:off
            permissionService.grantPermissions(rootAppRoleSid, requestApplication, CarbonPermission.getACLPermissionList(Arrays.asList(
                CarbonPermission.DISCOVER,
            	CarbonPermission.READ,
                CarbonPermission.UPDATE,
                CarbonPermission.EXTEND,
                CarbonPermission.DELETE,
                
                CarbonPermission.EXECUTE_SPARQL_QUERY,
                CarbonPermission.EXECUTE_SPARQL_UPDATE,
                
                CarbonPermission.CREATE_AGENTS,
                CarbonPermission.EDIT_AGENTS,
                CarbonPermission.DELETE_AGENTS,
                
                CarbonPermission.ACCESS_API
            )));
            //@formatter:on
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void addDefaultPermissionsToRootContainer(Sid rootAppRoleSid, URIObject rootContainerUO) throws CarbonException {
		try {
			//@formatter:off
            permissionService.grantPermissions(rootAppRoleSid, rootContainerUO, CarbonPermission.getACLPermissionList(Arrays.asList(
                CarbonPermission.DISCOVER,
                CarbonPermission.READ,
                CarbonPermission.UPDATE,
                CarbonPermission.EXTEND,
                CarbonPermission.DELETE,
                
                CarbonPermission.DOWNLOAD,
                
                CarbonPermission.CREATE_LDPRS,
                CarbonPermission.CREATE_LDPC,
                CarbonPermission.CREATE_WFLDPNR,
                CarbonPermission.CREATE_ACCESS_POINT,
                
                CarbonPermission.ADD_MEMBER,
                
                CarbonPermission.EXECUTE_SPARQL_QUERY,
                CarbonPermission.EXECUTE_SPARQL_UPDATE
            )));
            //@formatter:on
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

	}

	private void addDefaultPermissionsToRootAppRole(Sid rootAppRoleSid, ApplicationRole rootAppRole) throws CarbonException {
		try {
			//@formatter:off
            permissionService.grantPermissions(rootAppRoleSid, rootAppRole, CarbonPermission.getACLPermissionList(Arrays.asList(
                CarbonPermission.DISCOVER,
                CarbonPermission.READ,
                CarbonPermission.UPDATE,
                CarbonPermission.EXTEND,
                CarbonPermission.DELETE,
                
                CarbonPermission.ADD_AGENTS,
                CarbonPermission.REMOVE_AGENTS,
                
                CarbonPermission.CREATE_CHILDREN,
                
                CarbonPermission.MANAGE_ACLS,
                
                CarbonPermission.ADD_GROUPS,
                CarbonPermission.REMOVE_GROUPS
            )));
            //@formatter:on
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

}
