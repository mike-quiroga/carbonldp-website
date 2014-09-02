package com.base22.carbon.api.ldp.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.base22.carbon.api.ldp.handlers.POSTNonRdfRequestHandler;
import com.base22.carbon.api.ldp.handlers.POSTRdfRequestHandler;
import com.base22.carbon.constants.HttpHeaders;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;

@Controller
public class POSTController extends AbstractBaseRdfAPIController {

	@Autowired
	private POSTRdfRequestHandler postRDFRequestHandler;
	@Autowired
	private POSTNonRdfRequestHandler postNonRDFRequestHandler;

	public static final String FILE_PARAMETER = "file";
	public static final String FILE_NAME_PARAMETER = "name";

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, headers = "content-type!=multipart/form-data")
	public ResponseEntity<Object> handleNonMultipartPost(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleNonMultipartPost()");
		}

		// Check if the Content-Type header is missing
		String contentTypeHeader = request.getHeader(HttpHeaders.CONTENT_TYPE);
		if ( contentTypeHeader == null ) {
			String friendlyMessage = "A media type wasn't specified.";
			String debugMessage = "The Content-Type header doesn't specify a media type for the entity body.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< handleNonMultipartPost() > The Content-Type wasn't specified.");
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addHeaderIssue(HttpHeaders.CONTENT_TYPE, null, "required", null);
			return new ResponseEntity<Object>(errorObject, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		Lang language = postRDFRequestHandler.getLanguageFromContentType(contentTypeHeader);
		if ( language == null ) {
			// The contentType isn't supported by this handler
			try {
				return postNonRDFRequestHandler.handleNonMultipartPOST(applicationIdentifier, contentTypeHeader, request, response, entity);
			} catch (CarbonException e) {
				// TODO: FT
				return new ResponseEntity<Object>(e.getErrorObject(), e.getErrorObject().getHttpStatus());
			}
		} else {
			try {
				return postRDFRequestHandler.handleRdfPOST(applicationIdentifier, request, response, entity);
			} catch (CarbonException e) {
				// TODO: FT
				return new ResponseEntity<Object>(e.getErrorObject(), e.getErrorObject().getHttpStatus());
			}
		}

	}

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	public ResponseEntity<Object> handleMultipartPost(@PathVariable("application") String applicationIdentifier,
			@RequestParam(value = FILE_NAME_PARAMETER, required = false) String fileName,
			@RequestParam(value = FILE_PARAMETER, required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response,
			HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleMultipartPost()");
		}

		try {
			return postNonRDFRequestHandler.handleMultipartPost(applicationIdentifier, fileName, file, request, response, entity);
		} catch (CarbonException e) {
			// TODO: FT
			return new ResponseEntity<Object>(e.getErrorObject(), e.getErrorObject().getHttpStatus());
		}
	}
}
