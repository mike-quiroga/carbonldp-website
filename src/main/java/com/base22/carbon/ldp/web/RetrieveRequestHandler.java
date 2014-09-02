package com.base22.carbon.ldp.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeader;
import com.base22.carbon.HttpHeaderValue;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.HttpUtil;
import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.APIPreferences.RetrieveContainerPreference;
import com.base22.carbon.apps.Application;
import com.base22.carbon.authorization.acl.AclSR;
import com.base22.carbon.ldp.LDPContainer;
import com.base22.carbon.ldp.LDPContainerQueryOptions;
import com.base22.carbon.ldp.LDPRSource;
import com.base22.carbon.ldp.URIObject;
import com.base22.carbon.ldp.WrapperForLDPNR;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RetrieveRequestHandler extends AbstractRequestHandler {

	//@formatter:off
	public static final List<RetrieveContainerPreference> DEFAULT_RCP = new ArrayList<RetrieveContainerPreference>(Arrays.asList(
			RetrieveContainerPreference.CONTAINER_PROPERTIES,
			RetrieveContainerPreference.MEMBERSHIP_TRIPLES
	));
	//@formatter:on

	public ResponseEntity<Object> handleGET(String applicationIdentifier, HttpServletRequest request, HttpServletResponse response, HttpEntity<byte[]> entity)
			throws CarbonException {

		Application application = getApplicationFromContext();
		String dataset = application.getDatasetName();

		return this.handleLDPResourceRetrieval(dataset, request, response, entity);
	}

	private ResponseEntity<Object> handleLDPResourceRetrieval(String dataset, HttpServletRequest request, HttpServletResponse response,
			HttpEntity<byte[]> entity) throws CarbonException {

		String documentURI = HttpUtil.getRequestURL(request);
		Enumeration<String> linkHeaders = request.getHeaders(HttpHeaders.LINK);
		HttpHeader linkHeader = new HttpHeader(linkHeaders);

		// Get the preferred interaction model (if specified)
		InteractionModel interactionModel = getInteractionModel(linkHeader);

		// Get the URIObject of the document
		URIObject documentURIObject = null;
		try {
			documentURIObject = uriObjectDAO.findByURI(documentURI);
		} catch (AccessDeniedException e) {
			// TODO: FT - Log it? -
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// TODO: Decide. Should we take for granted that a document exists if it's uriObject does
		if ( documentURIObject == null ) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}

		// Get the types of the document
		Set<String> documentTypes;
		try {
			documentTypes = ldpService.getDocumentTypes(documentURIObject, dataset);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Default Interaction Model
		InteractionModel dim = getTargetDIM(documentURIObject, dataset);

		// Delegate to the appropriate handler (depending on the type of the document and the preferred interaction
		// model)
		if ( ldpService.documentIsContainer(documentTypes) ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- handleLDPResourceRetrieval() > The document is an LDPContainer");
			}
			if ( interactionModel != null ) {
				if ( interactionModel == InteractionModel.RDF_SOURCE ) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< handleLDPResourceRetrieval() > It was specified to use an interaction model of an LDPRS. Delegating to handleLDPRSGet()...");
					}
					return handleLDPRSourceRetrieval(documentURIObject, dataset, request, response, entity);
				} else if ( interactionModel == InteractionModel.CONTAINER ) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< handleLDPResourceRetrieval() > It was specified to use an interaction model of an LDPC. Delegating to handleLDPContainerGet()...");
					}
					return handleLDPContainerRetrieval(documentURIObject, dataset, documentTypes, request, response, entity);
				}
			}
			if ( dim == InteractionModel.RDF_SOURCE ) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< handleLDPResourceRetrieval() > The document has a dim of RDFSource. Delegating to handleLDPRSourceRetrieval()...");
				}
				return handleLDPRSourceRetrieval(documentURIObject, dataset, request, response, entity);
			} else {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< handleLDPResourceRetrieval() > It wasn't specified a preferred interaction model. Delegating to handleLDPContainerGet()...");
				}
				return handleLDPContainerRetrieval(documentURIObject, dataset, documentTypes, request, response, entity);
			}

		} else if ( ldpService.documentIsWrapperForLDPNR(documentTypes) ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- handleLDPResourceRetrieval() > The document is a WrapperForLDPNR");
			}
			if ( interactionModel != null ) {
				if ( interactionModel == InteractionModel.RDF_SOURCE ) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< handleLDPResourceRetrieval() > It was specified to use an interaction model of an LDPRS. Delegating to handleLDPRSGet()...");
					}
					return handleLDPRSourceRetrieval(documentURIObject, dataset, request, response, entity);
				} else if ( interactionModel == InteractionModel.CONTAINER ) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< handleLDPResourceRetrieval() > It was specified to use an interaction model of an LDPNR. Delegating to handleLDPNRGet()...");
					}
					return handleLDPNRRetrieval(documentURIObject, dataset, request, response, entity);
				}
			}
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< handleLDPResourceRetrieval() > It wasn't specified a preferred interaction model. Delegating to handleLDPNRGet()...");
			}
			return handleLDPNRRetrieval(documentURIObject, dataset, request, response, entity);
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- handleLDPResourceRetrieval() > The document is an LDPRSource");
				LOG.debug("<< handleLDPResourceRetrieval() > Delegating to handleLDPRSGet()...");
			}
			return handleLDPRSourceRetrieval(documentURIObject, dataset, request, response, entity);
		}
	}

	private ResponseEntity<Object> handleLDPRSourceRetrieval(URIObject documentURIObject, String dataset, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		Enumeration<String> preferHeaders = request.getHeaders(HttpHeaders.PREFER);
		HttpHeader preferHeader = new HttpHeader(preferHeaders);

		// Get the LDPRSource
		LDPRSource ldpRSource = null;
		try {
			ldpRSource = ldpService.getLDPRSource(documentURIObject, dataset);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if ( includeACL(preferHeader) ) {
			try {
				ldpPermissionService.injectACLToLDPResource(documentURIObject, ldpRSource);
			} catch (CarbonException e) {
				return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			HttpHeaderValue aclPreference = new HttpHeaderValue();
			aclPreference.setMainKey("return");
			aclPreference.setMainValue("representation");
			aclPreference.setExtendingKey("include");
			aclPreference.setExtendingValue(AclSR.Resources.CLASS.getPrefixedURI().getURI());

			response.addHeader("Preference-Applied", aclPreference.toString());
		}

		addAllowHeadersForLDPRS(documentURIObject, response);

		return new ResponseEntity<Object>(ldpRSource, HttpStatus.OK);
	}

	private ResponseEntity<Object> handleLDPContainerRetrieval(URIObject documentURIObject, String dataset, Set<String> documentTypes,
			HttpServletRequest request, HttpServletResponse response, HttpEntity<byte[]> entity) {

		Enumeration<String> preferHeaders = request.getHeaders(HttpHeaders.PREFER);
		HttpHeader preferHeader = new HttpHeader(preferHeaders);

		// Get the container type
		String containerType = ldpService.getDocumentContainerType(documentTypes);

		// Check for preferences to apply
		List<RetrieveContainerPreference> preferences = getRetrieveContainerPreferences(preferHeader);
		// TODO: Remove this block when RetrieveContainerPreference is accepted by the LDPService
		// ===========
		LDPContainerQueryOptions options = new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.GET);
		options.setContainedResources(false);
		options.setContainerProperties(false);
		options.setContainmentTriples(false);
		options.setMembershipTriples(false);
		options.setMemberResources(false);
		for (RetrieveContainerPreference preference : preferences) {
			switch (preference) {
				case CONTAINED_RESOURCES:
					options.setContainedResources(true);
					break;
				case CONTAINER_PROPERTIES:
					options.setContainerProperties(true);
					break;
				case CONTAINMENT_TRIPLES:
					options.setContainmentTriples(true);
					break;
				case MEMBERSHIP_TRIPLES:
					options.setMembershipTriples(true);
					break;
				case MEMBER_RESOURCES:
					options.setMemberResources(true);
					break;
				default:
					break;
			}

		}
		// ===========

		// Fetch the Document according to the preferences
		LDPContainer ldpContainer = null;
		try {
			ldpContainer = ldpService.getLDPContainer(documentURIObject, dataset, containerType, options);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Get the container contained and members count
		int count[];
		try {
			count = ldpService.countLDPContainer(documentURIObject, dataset, containerType);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if ( count.length == 2 ) {
			response.addHeader("X-Carbon-Contains-Count", String.valueOf(count[0]));
			response.addHeader("X-Carbon-Member-Count", String.valueOf(count[1]));
		}

		if ( includeACL(preferHeader) ) {
			try {
				ldpPermissionService.injectACLToLDPResource(documentURIObject, ldpContainer);
			} catch (CarbonException e) {
				return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			HttpHeaderValue aclPreference = new HttpHeaderValue();
			aclPreference.setMainKey("return");
			aclPreference.setMainValue("representation");
			aclPreference.setExtendingKey("include");
			aclPreference.setExtendingValue(AclSR.Resources.CLASS.getPrefixedURI().getURI());

			response.addHeader("Preference-Applied", aclPreference.toString());
		}

		addPreferencesApplied(response, preferences);

		addAllowHeadersForLDPC(documentURIObject, response);

		return new ResponseEntity<Object>(ldpContainer, HttpStatus.OK);

	}

	// TODO: Refactor
	// TODO: Add ACL restrictions
	private ResponseEntity<Object> handleLDPNRRetrieval(URIObject documentURIObject, String dataset, HttpServletRequest request, HttpServletResponse response,
			HttpEntity<byte[]> entity) throws CarbonException {

		String documentURI = documentURIObject.getURI();

		Application application = getApplicationFromContext();

		// Wrap the model into a WrapperForLDPNR
		WrapperForLDPNR rdfWrapper = null;
		try {
			rdfWrapper = ldpService.getWrapperForLDPNR(documentURIObject, dataset);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if ( rdfWrapper.getFileName() == null ) {
			String debugMessage = MessageFormat.format("The WrapperForLDPNR with URI: {0}, doesn't have a file attached to it.", documentURI);

			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< handleLDPNRRetrieval() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage("There was a problem while trying to process the request.");
			errorObject.setDebugMessage(debugMessage);

			return HttpUtil.createErrorResponseEntity(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Compose the file path
		StringBuilder filePathStringBuilder = new StringBuilder();
		filePathStringBuilder.append(configurationService.getApplicationUploadsPath(application)).append("/").append(rdfWrapper.getFileName());
		String filePath = filePathStringBuilder.toString();

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			String debugMessage = MessageFormat.format("The file for the WrapperForLDPNR with URI: {0}, couldn't be find.", documentURI);

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage("There was a problem while trying to process the request.");
			errorObject.setDebugMessage(debugMessage);

			return HttpUtil.createErrorResponseEntity(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		rdfWrapper.setFileInputStream(fileInputStream);

		response.addHeader(HttpHeaders.ALLOW, "GET, HEAD, PUT, PATCH, DELETE");

		return new ResponseEntity<Object>(rdfWrapper, HttpStatus.OK);
	}

	private InteractionModel getTargetDIM(URIObject documentURIObject, String dataset) throws CarbonException {
		InteractionModel dim = null;
		try {
			dim = ldpService.getDefaultInteractionModel(documentURIObject, dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return dim;
	}

	private List<RetrieveContainerPreference> getRetrieveContainerPreferences(HttpHeader preferHeader) {
		List<RetrieveContainerPreference> preferences = new ArrayList<RetrieveContainerPreference>(DEFAULT_RCP);

		if ( preferHeader != null ) {
			List<HttpHeaderValue> includePreferences = HttpHeader.filterHeaderValues(preferHeader, "return", "representation", "include", null);
			List<HttpHeaderValue> omitPreferences = HttpHeader.filterHeaderValues(preferHeader, "return", "representation", "omit", null);

			for (HttpHeaderValue omitPreference : omitPreferences) {
				RetrieveContainerPreference containerPreference = RetrieveContainerPreference.findByURI(omitPreference.getExtendingValue());
				if ( containerPreference != null ) {
					if ( preferences.contains(containerPreference) ) {
						preferences.remove(containerPreference);
					}
				}
			}

			for (HttpHeaderValue includePreference : includePreferences) {
				RetrieveContainerPreference containerPreference = RetrieveContainerPreference.findByURI(includePreference.getExtendingValue());
				if ( containerPreference != null ) {
					if ( ! preferences.contains(containerPreference) ) {
						preferences.add(containerPreference);
					}
				}
			}
		}

		return preferences;
	}

	private boolean includeACL(HttpHeader preferHeader) {
		boolean include = false;
		List<HttpHeaderValue> includePreferences = HttpHeader.filterHeaderValues(preferHeader, "return", "representation", "include", null);

		for (HttpHeaderValue includePreference : includePreferences) {
			String includeValue = includePreference.getExtendingValue();
			if ( includeValue != null ) {
				if ( AclSR.Resources.findByURI(includeValue) == AclSR.Resources.CLASS ) {
					include = true;
				}
			}
		}
		return include;
	}

	private void addPreferencesApplied(HttpServletResponse response, List<RetrieveContainerPreference> preferences) {
		for (RetrieveContainerPreference preference : preferences) {
			if ( ! DEFAULT_RCP.contains(preference) ) {
				HttpHeaderValue header = new HttpHeaderValue();
				header.setMainKey("return");
				header.setMainValue("representation");
				header.setExtendingKey("include");
				header.setExtendingValue(preference.getPrefixedURI().getURI());
				response.addHeader(HttpHeaders.PREFERENCE_APPLIED, header.toString());
			}
		}

		for (RetrieveContainerPreference defaultPreference : DEFAULT_RCP) {
			if ( ! preferences.contains(defaultPreference) ) {
				HttpHeaderValue header = new HttpHeaderValue();
				header.setMainKey("return");
				header.setMainValue("representation");
				header.setExtendingKey("include");
				header.setExtendingValue(defaultPreference.getPrefixedURI().getURI());
				response.addHeader(HttpHeaders.PREFERENCE_APPLIED, header.toString());
			}
		}
	}

}
