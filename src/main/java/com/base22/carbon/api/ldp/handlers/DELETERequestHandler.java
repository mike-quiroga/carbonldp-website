package com.base22.carbon.api.ldp.handlers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.base22.carbon.constants.APIPreferences.DeleteContainerPreference;
import com.base22.carbon.constants.HttpHeaders;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.models.LDPContainerQueryOptions;
import com.base22.carbon.models.WrapperForLDPNR;
import com.base22.carbon.security.models.Application;
import com.base22.carbon.security.models.URIObject;
import com.base22.carbon.utils.HttpUtil;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class DELETERequestHandler extends AbstractRequestHandler {

	//@formatter:off
	public static final List<DeleteContainerPreference> DEFAULT_DCP = new ArrayList<DeleteContainerPreference>(Arrays.asList(
		DeleteContainerPreference.MEMBERSHIP_TRIPLES
	));
	//@formatter:on

	public ResponseEntity<Object> handleDelete(String applicationIdentifier, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleDelete()");
		}

		resetGlobalVariables();
		setGlobalVariables(applicationIdentifier, request, response, requestEntity);

		Application application = getApplicationFromContext();
		String dataset = application.getDatasetName();

		String documentURI = HttpUtil.getRequestURL(request);

		String ifMatchHeader = request.getHeader(HttpHeaders.IF_MATCH);

		// Check if there is a named graph that matches the request URI
		URIObject documentURIObject = null;
		try {
			documentURIObject = uriObjectDAO.findByURI(documentURI);
		} catch (AccessDeniedException e) {
			// TODO: FT - Log it? -
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if ( documentURIObject == null ) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}

		if ( ifMatchHeader != null ) {
			// Format the ETag (If the client sent it within quotes)
			ifMatchHeader = ifMatchHeader.contains("\"") ? ifMatchHeader.split("\"")[1] : ifMatchHeader;

			// Get document's ETag
			String documentETag = null;
			try {
				documentETag = ldpService.getETagofLDPRSource(documentURIObject, dataset);
			} catch (CarbonException e) {
				return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if ( documentETag != null ) {
				// Compare the ETags
				if ( ! documentETag.equals(ifMatchHeader) ) {
					String debugMessage = MessageFormat.format("The If-Match header didn''t match the document resource ETag. If-Match: {0}, ETag: {1}",
							ifMatchHeader, documentETag);

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< handleDelete() > {}", debugMessage);
					}

					ErrorResponseFactory factory = new ErrorResponseFactory();
					ErrorResponse errorObject = factory.create();
					errorObject.setFriendlyMessage("The resource has been externally modified while processing the request. The request will be aborted.");
					errorObject.setDebugMessage(debugMessage);
					return HttpUtil.createErrorResponseEntity(errorObject, HttpStatus.PRECONDITION_FAILED);
				}

			} else {
				String debugMessage = MessageFormat.format("The resource with URI: ''{0}'', doesn''t have a valid ETag.", documentURI);

				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< handleDelete() > {}", debugMessage);
				}
			}

		}

		// Get the types of the document
		Set<String> documentTypes;
		try {
			documentTypes = ldpService.getDocumentTypes(documentURIObject, dataset);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Delegate to the appropriate handler (depending on the type of the document)
		if ( ldpService.documentIsContainer(documentTypes) ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- handleDelete() > The document is an LDPContainer");
			}
			return handleLDPContainerDeletion(documentURIObject, documentTypes, dataset, request, response);
		} else if ( ldpService.documentIsWrapperForLDPNR(documentTypes) ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- handleDelete() > The document is a WrapperForLDPNR");
			}
			return handleLDPNRDeletion(documentURIObject, dataset, request, response);
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- handleDelete() > The document is an LDPRSource");
			}
			return handleLDPRSourceDeletion(documentURIObject, dataset, request, response);
		}
	}

	private ResponseEntity<Object> handleLDPRSourceDeletion(URIObject documentURIObject, String dataset, HttpServletRequest request,
			HttpServletResponse response) {

		// Delete the document
		try {
			ldpService.deleteLDPRSource(documentURIObject, true, dataset);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	private ResponseEntity<Object> handleLDPContainerDeletion(URIObject documentURIObject, Set<String> documentTypes, String dataset,
			HttpServletRequest request, HttpServletResponse response) {

		Enumeration<String> preferHeaders = request.getHeaders(HttpHeaders.PREFER);
		HttpHeader preferHeader = new HttpHeader(preferHeaders);

		// Get the container type
		String containerType = ldpService.getDocumentContainerType(documentTypes);

		// Check for preferences to apply
		List<DeleteContainerPreference> preferences = getDeleteContainerPreferences(preferHeader);
		// TODO: Remove this block when DeleteContainerPreference is accepted by the LDPService
		// ===========
		LDPContainerQueryOptions options = new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.DELETE);
		for (DeleteContainerPreference preference : preferences) {
			switch (preference) {
				case CONTAINED_RESOURCES:
					options.setContainedResources(true);
					break;
				case CONTAINER:
					options.setContainerProperties(true);
					break;
				case MEMBERSHIP_RESOURCES:
					return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
				case MEMBERSHIP_TRIPLES:
					options.setMembershipTriples(true);
					break;
				default:
					break;
			}

		}
		// ===========

		// Try to delete the container with the options specified
		// TODO: Make the LDPService accept DeleteContainerPreference instead of LDPContainerQueryOptions
		try {
			ldpService.deleteLDPContainer(documentURIObject, dataset, containerType, options);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		addPreferencesApplied(response, preferences);

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	private ResponseEntity<Object> handleLDPNRDeletion(URIObject targetURIObject, String dataset, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		WrapperForLDPNR targetWrapper = null;

		targetWrapper = getTargetWrapper(targetURIObject);

		deleteLDPNR(targetURIObject, targetWrapper);

		deleteWrapperForLDPNR(targetURIObject, targetWrapper);

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	private WrapperForLDPNR getTargetWrapper(URIObject targetURIObject) throws CarbonException {
		WrapperForLDPNR wrapper = null;
		try {
			wrapper = ldpService.getWrapperForLDPNR(targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return wrapper;
	}

	private void deleteLDPNR(URIObject targetURIObject, WrapperForLDPNR targetWrapper) throws CarbonException {
		try {
			fileService.deleteFile(targetURIObject, this.application, targetWrapper);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void deleteWrapperForLDPNR(URIObject targetURIObject, WrapperForLDPNR targetWrapper) throws CarbonException {
		try {
			ldpService.deleteLDPRSource(targetURIObject, true, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private List<DeleteContainerPreference> getDeleteContainerPreferences(HttpHeader preferHeader) {
		List<DeleteContainerPreference> dcp = new ArrayList<DeleteContainerPreference>(DEFAULT_DCP);

		if ( preferHeader != null ) {
			List<HttpHeaderValue> includePreferences = HttpHeader.filterHeaderValues(preferHeader, "include", null, null, null);
			List<HttpHeaderValue> omitPreferences = HttpHeader.filterHeaderValues(preferHeader, "omit", null, null, null);

			for (HttpHeaderValue omitPreference : omitPreferences) {
				DeleteContainerPreference containerPreference = DeleteContainerPreference.findByURI(omitPreference.getMainValue());
				if ( containerPreference != null ) {
					if ( dcp.contains(containerPreference) ) {
						dcp.remove(containerPreference);
					}
				}
			}

			for (HttpHeaderValue includePreference : includePreferences) {
				DeleteContainerPreference containerPreference = DeleteContainerPreference.findByURI(includePreference.getMainValue());
				if ( containerPreference != null ) {
					if ( ! dcp.contains(containerPreference) ) {
						dcp.add(containerPreference);
					}
				}
			}
		}

		return dcp;
	}

	private void addPreferencesApplied(HttpServletResponse response, List<DeleteContainerPreference> preferences) {
		for (DeleteContainerPreference preference : preferences) {
			if ( ! DEFAULT_DCP.contains(preference) ) {
				HttpHeaderValue header = new HttpHeaderValue();
				header.setMainKey("include");
				header.setMainValue(preference.getPrefixedURI().getURI());
				response.addHeader(HttpHeaders.PREFERENCE_APPLIED, header.toString());
			}
		}

		for (DeleteContainerPreference defaultPreference : DEFAULT_DCP) {
			if ( ! preferences.contains(defaultPreference) ) {
				HttpHeaderValue header = new HttpHeaderValue();
				header.setMainKey("omit");
				header.setMainValue(defaultPreference.getPrefixedURI().getURI());
				response.addHeader(HttpHeaders.PREFERENCE_APPLIED, header.toString());
			}
		}
	}

}
