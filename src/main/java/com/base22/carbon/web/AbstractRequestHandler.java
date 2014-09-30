package com.base22.carbon.web;

import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ConfigurationService;
import com.base22.carbon.agents.AgentDAO;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationDAO;
import com.base22.carbon.apps.roles.ApplicationRoleDAO;
import com.base22.carbon.authentication.ApplicationContextToken;
import com.base22.carbon.authorization.LDPPermissionService;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.authorization.PlatformRoleDAO;
import com.base22.carbon.ldp.BasicContainerService;
import com.base22.carbon.ldp.DirectContainerService;
import com.base22.carbon.ldp.IndirectContainerService;
import com.base22.carbon.ldp.RDFSourceService;
import com.base22.carbon.ldp.URIObjectDAO;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.services.LDPService;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.sparql.SPARQLService;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractRequestHandler {

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
	protected URIObjectDAO uriObjectDAO;

	@Autowired
	protected LDPPermissionService ldpPermissionService;
	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected IndirectContainerService indirectContainerService;
	@Autowired
	protected DirectContainerService directContainerService;
	@Autowired
	protected BasicContainerService basicContainerService;
	@Autowired
	@Qualifier("s_RDFSourceService")
	protected RDFSourceService rdfSourceService;
	@Autowired
	protected LDPService ldpService;
	@Autowired
	protected SPARQLService sparqlService;
	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected ConfigurationService configurationService;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected boolean hasGenericRequestResources(Model requestModel) throws CarbonException {
		AntPathMatcher matcher = new AntPathMatcher();

		ResIterator iterator = requestModel.listSubjects();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			String resourceURI = resource.getURI();

			if ( matcher.match("/requests/*", resourceURI.replace(configurationService.getServerURL(), "")) ) {
				if ( HTTPUtil.isDocumentResourceURI(resourceURI) ) {
					return true;
				}
			}
		}

		return false;
	}

	protected Resource[] getDocumentResources(Model requestModel) {
		List<Resource> documentResources = new ArrayList<Resource>();

		ResIterator iterator = requestModel.listSubjects();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			String resourceURI = resource.getURI();

			if ( HTTPUtil.isDocumentResourceURI(resourceURI) ) {
				documentResources.add(resource);
			}
		}

		return documentResources.toArray(new Resource[documentResources.size()]);
	}

	protected Resource[] getInlineResourcesOf(Resource documentResource, Model requestModel) {
		List<Resource> inlineResources = new ArrayList<Resource>();

		String documentResourceURI = documentResource.getURI();

		ResIterator iterator = requestModel.listSubjects();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			String resourceURI = resource.getURI();

			if ( HTTPUtil.isInlineResourceURIOf(resourceURI, documentResourceURI) ) {
				inlineResources.add(resource);
			}
		}

		return inlineResources.toArray(new Resource[inlineResources.size()]);
	}

	protected Resource[] getExternalResources(Resource[] documentResources, Model requestModel) {
		List<Resource> externalResources = new ArrayList<Resource>();

		ResIterator iterator = requestModel.listSubjects();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			String resourceURI = resource.getURI();

			boolean sharesBase = false;
			for (Resource documentResource : documentResources) {
				String documentResourceURI = documentResource.getURI();
				if ( resourceURI.equals(documentResourceURI) ) {
					sharesBase = true;
					break;
				} else if ( HTTPUtil.isInlineResourceURIOf(resourceURI, documentResourceURI) ) {
					sharesBase = true;
					break;
				}
			}

			if ( ! sharesBase ) {
				externalResources.add(resource);
			}
		}

		return externalResources.toArray(new Resource[externalResources.size()]);
	}

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

			if ( matcher.match("/requests/*", resourceURI.replace(configurationService.getServerURL(), "")) ) {
				if ( HTTPUtil.isDocumentResourceURI(resourceURI) ) {
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
		}

		if ( documentResource == null ) {
			String friendlyMessage = "The entityBody of the request doesn't contain valid resources.";
			String debugMessage = "There must be a resource that follows the pattern /requests/<timestamp>.";
			String entityBodyMessage = "The request doesn't contain a resource that follows the pattern /requests/<timestamp>.";

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
		return documentResource;
	}

	protected boolean hasContentType(HttpServletRequest request) {
		return request.getContentType() != null;
	}

	protected ContentType getContentType(HttpServletRequest request) throws CarbonException {
		String contentTypeString = request.getContentType();

		if ( contentTypeString == null ) {
			String friendlyMessage = "A Content-Type wasn't specified.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.addHeaderIssue("Content-Type", null, "required", null);

			throw new CarbonException(errorObject);
		}

		ContentType contentType = null;
		try {
			contentType = ContentType.parse(contentTypeString);
		} catch (ParseException e) {
			String friendlyMessage = "The Content-Type specified isn't supported.";
			String debugMessage = MessageFormat.format("The Content-Type specfied: ''{0}'', couldn't be parsed.", contentTypeString);

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addHeaderIssue("Content-Type", null, "invalid", contentTypeString);

			throw new CarbonException(errorObject);
		} catch (UnsupportedCharsetException e) {
			String friendlyMessage = "The charset of the Content-Type specified isn't supported.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.addHeaderIssue("Content-Type", null, "unsupported", contentTypeString);

			throw new CarbonException(errorObject);
		}

		return contentType;
	}

	protected Application getApplicationFromContext() throws CarbonException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! (authentication instanceof ApplicationContextToken) ) {
			String friendlyMessage = "There was a problem processing your request. Please contact an administrator.";
			String debugMessage = "The application context was never set. Can't proceed with the request.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getApplicationFromContext() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
		Application application = ((ApplicationContextToken) authentication).getCurrentApplicationContext();
		return application;
	}

}
