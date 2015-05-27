package com.carbonldp.platform.api;

import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestHandler
public class PlatformAPIGetRequestHandler extends AbstractRequestHandler {

	@Autowired
	private PlatformAPIService platformAPIService;

	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		PlatformAPI platformAPI = platformAPIService.get();

		return new ResponseEntity<>( platformAPI, HttpStatus.OK );
	}
}
