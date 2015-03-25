package com.carbonldp.apps.web;

import com.carbonldp.ldp.web.AbstractGETRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestHandler
public class AppsGETHandler extends AbstractGETRequestHandler {
	@Override
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		return super.handleRequest( request, response );
	}
}
