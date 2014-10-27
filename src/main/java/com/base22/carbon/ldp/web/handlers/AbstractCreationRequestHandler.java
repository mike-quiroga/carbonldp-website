package com.base22.carbon.ldp.web.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RiotException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.CarbonException;
import com.base22.carbon.HTTPHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerFactory;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.RDFSourceFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.ConvertInputStream;
import com.base22.carbon.utils.ConvertString;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.utils.RDFUtil;
import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractCreationRequestHandler extends AbstractLDPRequestHandler {
	protected HttpServletResponse response;
	protected HttpServletRequest request;

	protected String dataset;

	protected Lang language;
	protected String charset;
	protected InteractionModel interactionModel;

	protected InputStream entityBodyInputStream;
	protected String entityBodyString;

	protected String targetURI;
	protected URIObject targetURIObject;
	protected RDFSource targetRDFSource;

	protected String requestURI;
	protected URIObject requestURIObject;
	protected Model requestModel;
	protected RDFSource requestRDFSource;
	protected Container requestContainer;

	protected RDFSourceFactory ldpRSourceFactory;
	protected ContainerFactory ldpContainerFactory;

	protected void resetGlobalVariables() {
		this.response = null;
		this.request = null;

		this.dataset = null;

		this.language = null;
		this.charset = null;
		this.interactionModel = null;

		this.entityBodyInputStream = null;
		this.entityBodyString = null;

		this.targetURI = null;
		this.targetURIObject = null;
		this.targetRDFSource = null;

		this.requestURI = null;
		this.requestURIObject = null;
		this.requestModel = null;
		this.requestRDFSource = null;
		this.requestContainer = null;
	}

	protected void populateDataset() throws CarbonException {
		Application application = null;
		try {
			application = getApplicationFromContext();
		} catch (CarbonException e) {
			// TODO: FT
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		this.dataset = application.getDatasetName();
	}

	protected void populateTargetURI() {
		this.targetURI = HTTPUtil.getRequestURL(this.request);
	}

	protected void populateTargetURIObject() throws CarbonException {
		// Get the URIObject of the document
		try {
			this.targetURIObject = uriObjectDAO.findByURI(this.targetURI);
		} catch (AccessDeniedException e) {
			// TODO: FT - Log it? -
			String friendlyMessage = "A resource with the request URI wasn't found.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< populateURIObject() > The authenticated agent doesn't have DISCOVER access to the resource with URI: {}", this.targetURI);
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
	}

	protected boolean targetDocumentExists() {
		return this.targetURIObject != null;
	}

	protected void populateLanguage() throws CarbonException {
		String contentTypeHeader = request.getHeader(HTTPHeaders.CONTENT_TYPE);

		this.language = getLanguageFromContentType(contentTypeHeader);
		if ( language == null ) {
			String friendlyMessage = "The request is in a format that is not supported.";
			String debugMessage = MessageFormat.format("The media type specified isn't supported. The supported media types are: ''{0}''",
					getSupportedMediaTypesString());
			String contentTypeMessage = "The type specified isn't supported.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< populateLanguage() > The content type specified isn't supported. Content-Type: {}", contentTypeHeader);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addHeaderIssue(HTTPHeaders.CONTENT_TYPE, null, contentTypeMessage, contentTypeHeader);

			throw new CarbonException(errorObject);
		}

		this.charset = getCharsetFromContentType(contentTypeHeader);
	}

	protected void addDefaultPrefixes() throws CarbonException {
		// Set default prefixes
		try {
			this.entityBodyInputStream = RDFUtil.setDefaultNSPrefixes(this.entityBodyInputStream, language, true);
		} catch (IOException e) {
			String debugMessage = "There was a problem while trying to include the default prefixes to the entity body.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx addDefaultPrefixes() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addDefaultPrefixes() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setFriendlyMessage("There was a problem while processing the request.");
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, debugMessage);
			errorObject.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);

			throw new CarbonException(errorObject);
		}

	}

	protected void populateEntityBody(HttpEntity<byte[]> entityBody) throws CarbonException {
		if ( (! entityBody.hasBody()) || entityBody.getBody().length == 0 ) {
			String debugMessage = "The request doesn't have an entity body.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< populateEntityBody() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(debugMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, "required");

			throw new CarbonException(errorObject);
		}

		this.entityBodyInputStream = new ByteArrayInputStream(entityBody.getBody());

		try {
			this.entityBodyInputStream = prepareEntityBodyInputStream(this.entityBodyInputStream, this.charset);
		} catch (CarbonException e) {
			throw e;
		}
	}

	@SuppressWarnings("resource")
	protected InputStream getBodyInputStream(HttpEntity<byte[]> entityBody) throws CarbonException {
		if ( (! entityBody.hasBody()) || entityBody.getBody().length == 0 ) {
			String debugMessage = "The request doesn't have an entity body.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< populateEntityBody() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(debugMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, "required");

			throw new CarbonException(errorObject);
		}

		InputStream bodyInputStream = new ByteArrayInputStream(entityBody.getBody());

		try {
			bodyInputStream = prepareEntityBodyInputStream(bodyInputStream);
		} catch (CarbonException e) {
			throw e;
		}

		return bodyInputStream;
	}

	protected void saveEntityBodyInString() throws CarbonException {
		this.entityBodyString = null;
		try {
			this.entityBodyString = ConvertInputStream.toString(this.entityBodyInputStream);
			this.entityBodyInputStream = ConvertString.toInputStream(this.entityBodyString);
		} catch (IOException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx populateEntityBody() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< populateEntityBody() > There was a problem while converting the entityBodyInputStream to a String.");
			}

			String friendlyMessage = "Unexpected Server Error.";
			String debugMessage = "An unexpected problem related with an InputStream rised when parsing the entity body.";

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
	}

	protected void resetEntityBodyInputStream() {
		this.entityBodyInputStream = ConvertString.toInputStream(this.entityBodyString);
	}

	protected void parseEntityBody(String baseURI) throws CarbonException {
		try {
			this.requestModel = RDFUtil.createInMemoryModel(this.entityBodyInputStream, this.language, baseURI);
		} catch (IOException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx parseEntityBody() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< parseEntityBody() > There was a problem while converting the entityBodyInputStream to a String.");
			}

			String friendlyMessage = "Unexpected Server Error.";
			String debugMessage = "An unexpected problem related with an InputStream rised when parsing the entity body.";

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		} catch (RiotException e) {
			// The entity body couldn't be parsed
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The entity body couldn't be parsed.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< parseEntityBody() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, e.getMessage());

			throw new CarbonException(errorObject);
		}
	}

	protected Model parseEntityBody(String baseURI, InputStream bodyInputStream, Lang language) throws CarbonException {
		Model model = null;

		try {
			model = RDFUtil.createInMemoryModel(bodyInputStream, language, baseURI);
		} catch (IOException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx parseEntityBody() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< parseEntityBody() > There was a problem while converting the entityBodyInputStream to a String.");
			}

			String friendlyMessage = "Unexpected Server Error.";
			String debugMessage = "An unexpected problem related with an InputStream rised when parsing the entity body.";

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		} catch (RiotException e) {
			// The entity body couldn't be parsed
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The entity body couldn't be parsed.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< parseEntityBody() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, e.getMessage());

			throw new CarbonException(errorObject);
		}

		return model;
	}

	protected void populateRequestRDFSource() throws CarbonException {
		try {
			this.requestRDFSource = getLDPRSourceFactory().create(this.requestURI, this.requestModel);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	protected void populateRequestContainer() throws CarbonException {
		try {
			this.requestContainer = getLDPContainerFactory().create(this.requestRDFSource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	protected void populateTargetRDFSource() throws CarbonException {
		this.targetRDFSource = null;
		try {
			this.targetRDFSource = ldpService.getLDPRSource(this.targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	protected RDFSourceFactory getLDPRSourceFactory() {
		if ( this.ldpRSourceFactory == null ) {
			this.ldpRSourceFactory = new RDFSourceFactory();
		}
		return this.ldpRSourceFactory;
	}

	protected ContainerFactory getLDPContainerFactory() {
		if ( this.ldpContainerFactory == null ) {
			this.ldpContainerFactory = new ContainerFactory();
		}
		return this.ldpContainerFactory;
	}
}