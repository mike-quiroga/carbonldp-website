package com.carbonldp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequestHandler
public class PlatformAPIGetRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) {
		// TODO: Return API description
		return new ResponseEntity<Object>(null, HttpStatus.OK);
	}
}
