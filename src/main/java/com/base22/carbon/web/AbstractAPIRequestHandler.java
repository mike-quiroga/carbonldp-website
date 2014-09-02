package com.base22.carbon.web;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ConfigurationService;
import com.base22.carbon.HttpUtil;
import com.base22.carbon.agents.AgentDAO;
import com.base22.carbon.apps.ApplicationDAO;
import com.base22.carbon.apps.roles.ApplicationRoleDAO;
import com.base22.carbon.authorization.LDPPermissionService;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.authorization.PlatformRoleDAO;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.LDPService;
import com.base22.carbon.repository.RepositoryService;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractAPIRequestHandler {

	@Autowired
	@Qualifier("platformPlatformRoleDAO")
	protected PlatformRoleDAO unsecuredPlatformRoleDAO;
	@Autowired
	@Qualifier("agentDetailsDAO")
	protected AgentDAO unsecuredAgentDetailsDAO;
	@Autowired
	@Qualifier("applicationDAO")
	protected ApplicationDAO securedApplicationDAO;
	@Autowired
	@Qualifier("applicationRoleDAO")
	protected ApplicationRoleDAO securedApplicationRoleDAO;
	@Autowired
	@Qualifier("platformApplicationRoleDAO")
	protected ApplicationRoleDAO unsecuredApplicationRoleDAO;

	@Autowired
	protected LDPPermissionService ldpPermissionService;
	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected LDPService ldpService;
	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected ConfigurationService configurationService;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected Resource getRequestModelMainResource(Model requestModel) throws CarbonException {
		Resource documentResource = null;
		AntPathMatcher matcher = new AntPathMatcher();

		ResIterator iterator = requestModel.listSubjects();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			String resourceURI = resource.getURI();

			if ( ! resourceURI.startsWith(configurationService.getServerURL()) ) {
				String friendlyMessage = "The entityBody of the request doesn't contain valid resources.";
				String debugMessage = "Every request resource must start with Carbon's domain.";
				String entityBodyMessage = MessageFormat.format("The resource with URI: ''{0}'', doesn''t start with Carbon''s domain.", resourceURI);

				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx getRequestModelMainResource() > {}", entityBodyMessage);
				}

				ErrorResponseFactory errorFactory = new ErrorResponseFactory();
				ErrorResponse errorObject = errorFactory.create();
				errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
				errorObject.setFriendlyMessage(friendlyMessage);
				errorObject.setDebugMessage(debugMessage);
				errorObject.setEntityBodyIssue(null, entityBodyMessage);

				throw new CarbonException(errorObject);
			}

			if ( ! matcher.match("/requests/*", resourceURI.replace(configurationService.getServerURL(), "")) ) {
				String friendlyMessage = "The entityBody of the request doesn't contain valid resources.";
				String debugMessage = "Every request resource must follow the pattern /requests/{timestamp}.";
				String entityBodyMessage = MessageFormat.format("The resource with URI: ''{0}'', doesn''t follow the pattern /requests/'{'timestamp'}'.",
						resourceURI);

				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx getRequestModelMainResource() > {}", entityBodyMessage);
				}

				ErrorResponseFactory errorFactory = new ErrorResponseFactory();
				ErrorResponse errorObject = errorFactory.create();
				errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
				errorObject.setFriendlyMessage(friendlyMessage);
				errorObject.setDebugMessage(debugMessage);
				errorObject.setEntityBodyIssue(null, entityBodyMessage);

				throw new CarbonException(errorObject);
			}

			if ( HttpUtil.isDocumentResourceURI(resourceURI) ) {
				if ( documentResource != null ) {
					String friendlyMessage = "The entityBody of the request isn't valid.";
					String debugMessage = "The request entityBody contains multiple document resources.";

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx getRequestModelMainResource() > {}", debugMessage);
					}

					ErrorResponseFactory errorFactory = new ErrorResponseFactory();
					ErrorResponse errorObject = errorFactory.create();
					errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
					errorObject.setFriendlyMessage(friendlyMessage);
					errorObject.setDebugMessage(debugMessage);
					errorObject.setEntityBodyIssue(null, debugMessage);

					throw new CarbonException(errorObject);
				}

				documentResource = resource;
			}
		}
		return documentResource;
	}

}
