package com.base22.carbon.security.handlers;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.security.dao.AgentLoginDetailsDAO;
import com.base22.carbon.security.dao.ApplicationDAO;
import com.base22.carbon.security.dao.ApplicationRoleDAO;
import com.base22.carbon.security.dao.PlatformRoleDAO;
import com.base22.carbon.security.services.LDPPermissionService;
import com.base22.carbon.security.services.PermissionService;
import com.base22.carbon.services.ConfigurationService;
import com.base22.carbon.services.LDPService;
import com.base22.carbon.services.RdfService;
import com.base22.carbon.services.RepositoryService;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractApplicationAPIRequestHandler {

	@Autowired
	@Qualifier("platformPlatformRoleDAO")
	protected PlatformRoleDAO unsecuredPlatformRoleDAO;
	@Autowired
	@Qualifier("agentDetailsDAO")
	protected AgentLoginDetailsDAO unsecuredAgentDetailsDAO;
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
	protected RdfService rdfService;
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

				ErrorResponse errorObject = new ErrorResponse();
				errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
				errorObject.setFriendlyMessage(friendlyMessage);
				errorObject.setDebugMessage(debugMessage);
				errorObject.setEntityBodyIssue(null, entityBodyMessage);

				throw new CarbonException(errorObject);
			}

			if ( ! matcher.match("/api/requests/*", resourceURI.replace(configurationService.getServerURL(), "")) ) {
				String friendlyMessage = "The entityBody of the request doesn't contain valid resources.";
				String debugMessage = "Every request resource must follow the pattern /api/requests/{timestamp}.";
				String entityBodyMessage = MessageFormat.format("The resource with URI: ''{0}'', doesn''t follow the pattern /api/requests/'{'timestamp'}'.",
						resourceURI);

				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx getRequestModelMainResource() > {}", entityBodyMessage);
				}

				ErrorResponse errorObject = new ErrorResponse();
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

					ErrorResponse errorObject = new ErrorResponse();
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
