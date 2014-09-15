package com.base22.carbon.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.utils.HTTPUtil;

@Controller
@RequestMapping(value = "api")
public class PlatformAPIController extends AbstractController {

	@Autowired
	private PlatformAPIGetRequestHandler getHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> getAPIDescription(HttpServletRequest request, HttpServletResponse response) {
		try {
			return getHandler.handleRequest(request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}
