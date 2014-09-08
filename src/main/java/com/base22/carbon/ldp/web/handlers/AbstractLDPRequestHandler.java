package com.base22.carbon.ldp.web.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.authentication.ApplicationContextToken;
import com.base22.carbon.authorization.LDPPermissionService;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.authorization.acl.CarbonACLPermissionFactory.CarbonPermission;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.repository.services.FileService;
import com.base22.carbon.repository.services.RDFService;
import com.base22.carbon.utils.ConvertInputStream;
import com.base22.carbon.utils.ConvertString;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.web.AbstractRequestHandler;
import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractLDPRequestHandler extends AbstractRequestHandler {

	@Autowired
	protected RDFService rdfService;

	@Autowired
	protected LDPPermissionService ldpPermissionService;
	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected FileService fileService;

	protected List<Lang> supportedRDFLanguages = null;
	protected List<MediaType> supportedMediaTypes = null;

	protected Application application;
	protected String dataset;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpEntity<byte[]> requestEntity;

	protected void setGlobalVariables(String applicationIdentifier, HttpServletRequest request, HttpServletResponse response, HttpEntity<byte[]> requestEntity)
			throws CarbonException {
		this.application = getApplicationFromContext();
		this.dataset = application.getDatasetName();

		this.request = request;
		this.response = response;
		this.requestEntity = requestEntity;
	}

	protected void resetGlobalVariables() {
		this.dataset = null;
		this.request = null;
		this.response = null;
		this.requestEntity = null;
	}

	protected String getCharsetFromContentType(String contentTypeHeader) {
		// TODO: Implement this method
		// Content-Type: text/turtle; charset=utf-8
		return "UTF-8";
	}

	protected boolean canReadMediaType(String contentTypeHeader) {
		MediaType contentType = getMediaTypeFromContentType(contentTypeHeader);
		if ( contentType == null ) {
			return false;
		}
		for (MediaType mediaType : getSupportedMediaTypes()) {
			if ( contentType.isCompatibleWith(mediaType) ) {
				return true;
			}
		}
		return false;
	}

	public Lang getLanguageFromContentType(String contentTypeHeader) {
		MediaType contentType = getMediaTypeFromContentType(contentTypeHeader);
		if ( contentType == null ) {
			return null;
		}
		for (Lang language : getSupportedRDFLanguages()) {
			if ( contentType.isCompatibleWith(contentTypeToMediaType(language.getContentType())) ) {
				return language;
			}
		}
		return null;
	}

	protected InteractionModel getInteractionModel(HttpHeader linkHeader) {
		if ( linkHeader != null ) {
			List<HttpHeaderValue> typeValues = HttpHeader.filterHeaderValues(linkHeader, null, null, "rel", "type");

			for (HttpHeaderValue typeValue : typeValues) {
				InteractionModel interactionModel = InteractionModel.findByURI(typeValue.getMainValue());
				if ( interactionModel != null ) {
					return interactionModel;
				}
			}
		}

		return null;
	}

	public Application getApplicationFromContext() throws CarbonException {
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

	protected InputStream prepareEntityBodyInputStream(InputStream entityBodyInputStream) throws CarbonException {
		return prepareEntityBodyInputStream(entityBodyInputStream, "UTF-8");
	}

	protected InputStream prepareEntityBodyInputStream(InputStream entityBodyInputStream, String charset) throws CarbonException {
		String entityBodyString = null;
		try {
			entityBodyString = ConvertInputStream.toString(entityBodyInputStream);
		} catch (IOException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx prepareEntityBodyInputStream() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- prepareEntityBodyInputStream() > There was a problem while converting the entityBodyInputStream to a String.");
			}
		}

		// Decode the entityBody (for possible AJAX origin)
		try {
			entityBodyString = URLDecoder.decode(entityBodyString, charset);
		} catch (UnsupportedEncodingException e) {
			String debugMessage = MessageFormat.format("The character encoding specified isn''t supported. Charset: ''{0}''", charset);

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx prepareEntityBodyInputStream() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< prepareEntityBodyInputStream() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setDebugMessage(debugMessage);
			errorObject.addParameterIssue("Content-Type", null, debugMessage, charset);

			throw new CarbonException(errorObject);
		}

		// Remove trailing equal sign when using AJAX calls
		if ( entityBodyString.endsWith("=") ) {
			entityBodyString = entityBodyString.substring(0, entityBodyString.length() - 1);
		}
		entityBodyInputStream = ConvertString.toInputStream(entityBodyString);

		return entityBodyInputStream;
	}

	protected MediaType getMediaTypeFromContentType(String contentTypeHeader) {
		MediaType mediaType = null;
		if ( contentTypeHeader == null ) {
			return mediaType;
		}
		String[] contentTypeArray = contentTypeHeader.split("/");

		if ( contentTypeArray.length != 2 ) {
			return mediaType;
		}

		String[] contentSubTypeArray = contentTypeArray[1].split(";");
		String contentSubType = null;
		contentSubType = contentSubTypeArray[0];
		mediaType = new MediaType(contentTypeArray[0], contentSubType);
		return mediaType;
	}

	protected List<MediaType> getSupportedMediaTypes() {
		if ( supportedMediaTypes == null ) {
			setSupportedMediaTypes();
		}

		return this.supportedMediaTypes;
	}

	protected String getSupportedMediaTypesString() {
		StringBuilder mediaTypesStringBuilder = new StringBuilder();
		for (MediaType mediaType : getSupportedMediaTypes()) {
			mediaTypesStringBuilder.append(mediaType.getType()).append("/").append(mediaType.getSubtype()).append(", ");
		}
		if ( mediaTypesStringBuilder.length() != 0 ) {
			// Remove the last ", "
			mediaTypesStringBuilder.delete(mediaTypesStringBuilder.length() - 2, mediaTypesStringBuilder.length() - 1);
		}
		return mediaTypesStringBuilder.toString();
	}

	protected void setSupportedMediaTypes() {
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		for (Lang language : getSupportedRDFLanguages()) {
			MediaType mediaType = new MediaType(language.getContentType().getType(), language.getContentType().getSubType());
			mediaTypes.add(mediaType);
		}
		this.supportedMediaTypes = mediaTypes;
	}

	protected Lang getDefaultLanguage() {
		return getSupportedRDFLanguages().get(0);
	}

	protected List<Lang> getSupportedRDFLanguages() {
		if ( supportedRDFLanguages == null ) {
			//@formatter:off
			setSupportedRDFLanguages(
				Arrays.asList(
					Lang.TURTLE,
					Lang.JSONLD,
					Lang.RDFJSON,
					Lang.RDFXML
				)
			);
			//@formatter:on
		}
		return supportedRDFLanguages;
	}

	protected void addAllowHeadersForLDPRS(URIObject documentURIObject, HttpServletResponse response) {
		if ( permissionService.hasPermission(CarbonPermission.READ.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "GET");
			response.addHeader(HttpHeaders.ALLOW, "HEAD");
		}
		if ( permissionService.hasPermission(CarbonPermission.CREATE_ACCESS_POINT.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "POST");
			response.addHeader(HttpHeaders.ACCEPT_POST, "text/turtle");
			response.addHeader(HttpHeaders.ACCEPT_POST, "application/ld+json");
		}
		if ( permissionService.hasPermission(CarbonPermission.UPDATE.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "PUT");
			response.addHeader(HttpHeaders.ACCEPT_PUT, "text/turtle");
			response.addHeader(HttpHeaders.ACCEPT_PUT, "application/ld+json");
			response.addHeader(HttpHeaders.ALLOW, "PATCH");
			response.addHeader(HttpHeaders.ACCEPT_PATCH, "text/turtle");
			response.addHeader(HttpHeaders.ACCEPT_PATCH, "application/ld+json");
		}
		if ( permissionService.hasPermission(CarbonPermission.DELETE.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "DELETE");
		}
	}

	protected void addAllowHeadersForLDPC(URIObject documentURIObject, HttpServletResponse response) {
		if ( permissionService.hasPermission(CarbonPermission.READ.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "GET");
			response.addHeader(HttpHeaders.ALLOW, "HEAD");
		}
		// TODO: All of this capabilities should be informed separately
		if ( permissionService.hasPermission(CarbonPermission.CREATE_LDPRS.getPermission(), documentURIObject)
				|| permissionService.hasPermission(CarbonPermission.CREATE_LDPC.getPermission(), documentURIObject)
				|| permissionService.hasPermission(CarbonPermission.CREATE_WFLDPNR.getPermission(), documentURIObject)
				|| permissionService.hasPermission(CarbonPermission.CREATE_ACCESS_POINT.getPermission(), documentURIObject)
				|| permissionService.hasPermission(CarbonPermission.ADD_MEMBER.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "POST");
			response.addHeader(HttpHeaders.ACCEPT_POST, "text/turtle");
			response.addHeader(HttpHeaders.ACCEPT_POST, "application/ld+json");
		}
		if ( permissionService.hasPermission(CarbonPermission.UPDATE.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "PUT");
			response.addHeader(HttpHeaders.ACCEPT_PUT, "text/turtle");
			response.addHeader(HttpHeaders.ACCEPT_PUT, "application/ld+json");
			response.addHeader(HttpHeaders.ALLOW, "PATCH");
			response.addHeader(HttpHeaders.ACCEPT_PATCH, "text/turtle");
			response.addHeader(HttpHeaders.ACCEPT_PATCH, "application/ld+json");
		}
		if ( permissionService.hasPermission(CarbonPermission.DELETE.getPermission(), documentURIObject) ) {
			response.addHeader(HttpHeaders.ALLOW, "DELETE");
		}
	}

	protected void setSupportedRDFLanguages(List<Lang> languages) {
		this.supportedRDFLanguages = languages;
	}

	private MediaType contentTypeToMediaType(ContentType contentType) {
		return new MediaType(contentType.getType(), contentType.getSubType());
	}

	protected String getTargetURI(HttpServletRequest request) {
		return HTTPUtil.getRequestURL(request);
	}

	protected URIObject getTargetURIObject(String targetURI) throws CarbonException {
		URIObject targetURIObject = null;
		try {
			targetURIObject = uriObjectDAO.findByURI(targetURI);
		} catch (AccessDeniedException e) {
			String friendlyMessage = "A resource with the request URI wasn't found.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getTargetURIObject() > The authenticated agent doesn't have DISCOVER access to the resource with URI: {}", targetURI);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setHttpStatus(HttpStatus.NOT_FOUND);

			throw new CarbonException(errorObject);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetURIObject;
	}

	protected boolean targetResourceExists(URIObject targetURIObject) {
		return targetURIObject != null;
	}

	protected ResponseEntity<Object> handleNonExistentResource(String targetURI, Model requestModel, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The RDFSource specified wasn't found.";
		String debugMessage = MessageFormat.format("The RDFSource with URI: ''{0}'', wasn''t found.", targetURI);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleNonExistentResource() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	protected boolean compareETags(String requestETag, String targetETag) {
		if ( requestETag == null ) {
			return false;
		}
		requestETag = requestETag.contains("\"") ? requestETag.split("\"")[1] : requestETag;
		requestETag = requestETag.toLowerCase();

		targetETag = targetETag.toLowerCase();

		return targetETag.equals(requestETag);
	}
}
