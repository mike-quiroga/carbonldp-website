package com.base22.carbon.ldp.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.web.handlers.POSTNonRdfRequestHandler;
import com.base22.carbon.ldp.web.handlers.POSTRdfRequestHandler;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
public class POSTController extends AbstractLDPController {

	@Autowired
	private POSTRdfRequestHandler postRDFRequestHandler;
	@Autowired
	private POSTNonRdfRequestHandler postNonRDFRequestHandler;

	public static final String FILE_PARAMETER = "file";
	public static final String FILE_NAME_PARAMETER = "name";

	// TODO: Add all the MediaTypes allowed

	//@formatter:off
	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, 
			consumes = { 
				"text/turtle"
			}
	)
	//@formatter:on
	public ResponseEntity<Object> handleTurtleRDFPost(String appSlug, HttpServletRequest request, HttpServletResponse response, HttpEntity<byte[]> entity) {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleTurtleRDFPost()");
		}

		try {
			return postRDFRequestHandler.handleTurtleRDFPost(appSlug, request, response, entity);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	//@formatter:off
	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, 
			consumes = { 
				"application/ld+json"
			}
	)
	//@formatter:on
	public ResponseEntity<Object> handleRDFPost(String appSlug, @RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleRDFPost()");
		}

		try {
			return postRDFRequestHandler.handleRDFPost(appSlug, requestModel, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, consumes = "multipart/form-data")
	public ResponseEntity<Object> handleMultipartPost(@PathVariable("application") String appSlug,
			@RequestParam(value = FILE_NAME_PARAMETER, required = false) String fileName,
			@RequestParam(value = FILE_PARAMETER, required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response,
			HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleMultipartPost()");
		}

		try {
			return postNonRDFRequestHandler.handleMultipartPost(appSlug, fileName, file, request, response, entity);
		} catch (CarbonException e) {
			// TODO: FT
			return new ResponseEntity<Object>(e.getErrorObject(), e.getErrorObject().getHttpStatus());
		}
	}
}
