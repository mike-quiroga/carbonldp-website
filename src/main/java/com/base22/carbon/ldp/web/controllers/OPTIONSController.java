package com.base22.carbon.ldp.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.web.handlers.OPTIONSRequestHandler;
import com.base22.carbon.utils.HTTPUtil;

@Controller
public class OPTIONSController extends AbstractLDPController {

	@Autowired
	private OPTIONSRequestHandler requestHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.OPTIONS)
	public ResponseEntity<Object> handleOptions(@PathVariable("application") String applicationIdentifier, Model model, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleOptions()");
		}

		try {
			return requestHandler.handleOPTIONS(applicationIdentifier, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

}