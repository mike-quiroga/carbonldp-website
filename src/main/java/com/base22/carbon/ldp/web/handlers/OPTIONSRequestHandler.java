package com.base22.carbon.ldp.web.handlers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.utils.HTTPUtil;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class OPTIONSRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleOPTIONS(String applicationIdentifier, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleOPTIONS()");
		}

		Application application = getApplicationFromContext();
		String dataset = application.getDatasetName();

		String documentURI = HTTPUtil.getRequestURL(request);

		// Get the URIObject of the document
		URIObject documentURIObject = null;
		try {
			documentURIObject = uriObjectDAO.findByURI(documentURI);
		} catch (AccessDeniedException e) {
			// TODO: FT - Log it? -
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// TODO: Decide. Should we take for granted that a document exists if its uriObject does
		if ( documentURIObject == null ) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}

		// Get the types of the document
		Set<String> documentTypes;
		try {
			documentTypes = ldpService.getDocumentTypes(documentURIObject, dataset);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// LDP servers that support POST MUST include an Accept-Post response header on HTTP OPTIONS responses,
		// listing post document media type(s) supported by the server.

		// LDP servers that support PATCH MUST include an Accept-Patch HTTP response header [RFC5789] on
		// HTTP OPTIONS requests, listing patch document media type(s) supported by the server.

		if ( ldpService.documentIsContainer(documentTypes) ) {
			addAllowHeadersForLDPC(documentURIObject, response);
		} else {
			addAllowHeadersForLDPRS(documentURIObject, response);
		}

		return new ResponseEntity<Object>(HttpStatus.OK);
	}
}
