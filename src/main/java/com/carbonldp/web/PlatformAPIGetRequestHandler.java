package com.carbonldp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class PlatformAPIGetRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) {
		// TODO: Return API description
		return new ResponseEntity<Object>(null, HttpStatus.OK);
	}
}
