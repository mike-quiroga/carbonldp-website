package com.base22.carbon.ldp.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.web.handlers.PATCHRequestHandler;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
public class PATCHController extends AbstractLDPController {

	@Autowired
	private PATCHRequestHandler requestHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.PATCH)
	public ResponseEntity<Object> handlePATCH(@PathVariable("application") String appSlug, @RequestBody Model model, HttpServletRequest request,
			HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handlePATCH()");
		}

		try {
			return requestHandler.handlePATCHRequest(appSlug, model, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}
