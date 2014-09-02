package com.base22.carbon.ldp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class PATCHRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handlePATCHRequest(String applicationIdentifier, Model model, HttpServletRequest request, HttpServletResponse response,
			HttpEntity<byte[]> entity) {

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);

		//@formatter:off
		/*
		
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handlePatch()");
		}
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("-- handlePatch() > request info: {}", HttpUtil.printRequestInfo(request));
		}

		String contentTypeHeader = request.getHeader(HttpHeaders.CONTENT_TYPE);
		String charset = null;

		if ( contentTypeHeader != null ) {
			charset = getCharsetFromContentType(contentTypeHeader);
		}

		if ( ! entity.hasBody() ) {
			LOG.error("<< handlePatch() > The request doesn't have an entity body.");
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}

		byte[] eBody = entity.getBody();
		InputStream entityBodyInputStream = new ByteArrayInputStream(eBody);

		try {
			if ( charset == null ) {
				entityBodyInputStream = prepareEntityBodyInputStream(entityBodyInputStream);
			} else {
				entityBodyInputStream = prepareEntityBodyInputStream(entityBodyInputStream, charset);
			}
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
		}

		// Before parsing, save the inputStrem by converting it to a String
		String entityBodyString = null;
		try {
			entityBodyString = ConvertInputStream.toString(entityBodyInputStream);
			entityBodyInputStream = ConvertString.toInputStream(entityBodyString);
		} catch (IOException e) {

		}

		String requestURI = HttpUtil.getRequestURL(request);

		com.hp.hpl.jena.rdf.model.Model oldDocumentModel;

		TurtlePatch turtlePatch = null;
		try {
			turtlePatch = new TurtlePatch(entityBodyString, requestURI);
		} catch (Exception exception) {
			LOG.error("<< handlePatch() > The entity body couldn't be parsed.");
			return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.BAD_REQUEST);
		}

		// Does the document exist?
		try {
			if ( ! rdfService.namedModelExists(requestURI, dataset) ) {
				LOG.error("<< handlePatch() > The document doesn't exist.");
				return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
			}
		} catch (CarbonException exception) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Get the documentModel to update
		try {
			oldDocumentModel = rdfService.getNamedModel(requestURI, dataset);
		} catch (CarbonException exception) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Check the precondition
		String etagToMatch = request.getHeader(HttpHeaders.IF_MATCH);
		if ( etagToMatch == null ) {
			// It already exists and the client didn't send an ETag to match
			return new ResponseEntity<Object>(
					"Aan If-Match header wasn't provided. The If-Match header must contain a long number that points to the desired document resource version to update.",
					HttpStatus.PRECONDITION_REQUIRED);
		}

		Resource oldResource = oldDocumentModel.getResource(requestURI);

		Long resourceETag = null, requestETag = null;

		try {
			requestETag = Long.parseLong(etagToMatch);
		} catch (NumberFormatException e) {
			return new ResponseEntity<Object>("The supplied If-Match header is incorrect. A long number is expected.", HttpStatus.BAD_REQUEST);
		}
		try {
			resourceETag = oldResource.getProperty(LDPRS.MODIFIED_P).getLong();
		} catch (NullPointerException exception) {
			LOG.error(FATAL, "<< handlePatch() > Resource's etag property doesn't exist. ResourceURI: {}", requestURI);
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NumberFormatException exception) {
			LOG.error(FATAL, "<< handlePatch() > Resource's etag property doesn't have the proper format (expecting long). ResourceURI: {}. ETag: {}",
					requestURI, oldResource.getProperty(LDPRS.MODIFIED_P).toString());
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if ( ! resourceETag.equals(requestETag) ) {
			// ETags didn't matched!
			LOG.error("<< handlePatch() > The If-Match header didn't match the resource ETag. If-Match: {}, ETag: {}", requestETag, resourceETag);
			return new ResponseEntity<Object>("The If-Match header provided and the resource version didn't match (maybe it was alterated during request?).",
					HttpStatus.PRECONDITION_FAILED);
		}

		// ETags matched, continue updating the resource
		turtlePatch.setDefaultPrefixes(Carbon.CONFIGURED_PREFIXES);
		String deleteQuery = turtlePatch.getDeleteQuery();
		String insertQuery = turtlePatch.getInsertQuery();

		SparqlQuery deleteSparqlQuery = new SparqlQuery(SparqlQuery.TYPE.UPDATE, dataset, deleteQuery);
		SparqlQuery insertSparqlQuery = new SparqlQuery(SparqlQuery.TYPE.UPDATE, dataset, insertQuery);

		if ( deleteQuery != null ) {
			try {
				sparqlService.update(deleteSparqlQuery, requestURI);
			} catch (CarbonException exception) {
				return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		if ( insertQuery != null ) {
			try {
				sparqlService.update(insertSparqlQuery, requestURI);
			} catch (CarbonException exception) {
				return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		Long newETag = null;
		try {
			newETag = ldpService.touchLDPRSource(requestURI, dataset);
		} catch (CarbonException exception) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.LOCATION, requestURI);
		headers.add(HttpHeaders.ETAG, HttpUtil.formatWeakETag(String.valueOf(newETag)));
		headers.add(HttpHeaders.ACCEPT_PATCH, "text/turtle");

		return new ResponseEntity<Object>("The resource has been updated.", headers, HttpStatus.OK);
		
		*/
		//@formatter:on
	}
}
