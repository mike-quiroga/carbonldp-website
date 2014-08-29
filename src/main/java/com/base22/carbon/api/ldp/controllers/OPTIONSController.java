package com.base22.carbon.api.ldp.controllers;

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

import com.base22.carbon.api.ldp.handlers.OPTIONSRequestHandler;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.utils.HttpUtil;

@Controller
public class OPTIONSController extends AbstractBaseRdfAPIController {

	@Autowired
	private OPTIONSRequestHandler requestHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.OPTIONS)
	public ResponseEntity<Object> handleOptions(@PathVariable("application") String applicationIdentifier, Model model, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleOptions()");
		}

		try {
			return requestHandler.handleOptions(applicationIdentifier, model, request, response, entity);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

}
