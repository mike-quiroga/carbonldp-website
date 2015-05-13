package com.carbonldp.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = "api" )
public class PlatformAPIController extends AbstractController {

	@Autowired
	private PlatformAPIGetRequestHandler getHandler;

	@RequestMapping( method = RequestMethod.GET )
	public ResponseEntity<Object> getAPIDescription(HttpServletRequest request, HttpServletResponse response) {
		return getHandler.handleRequest( request, response );
	}

	@RequestMapping( method = RequestMethod.HEAD )
	public ResponseEntity<Object> headAPIDescription(HttpServletRequest request, HttpServletResponse response) {
		return getHandler.handleRequest( request, response );
	}
}
