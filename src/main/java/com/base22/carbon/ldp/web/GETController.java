package com.base22.carbon.ldp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpUtil;

@Controller
public class GETController extends AbstractBaseRdfAPIController {

	@Autowired
	private RetrieveRequestHandler retrieveHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.GET)
	public ResponseEntity<Object> handleGET(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleGET()");
		}

		try {
			return retrieveHandler.handleGET(applicationIdentifier, request, response, entity);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}
}
