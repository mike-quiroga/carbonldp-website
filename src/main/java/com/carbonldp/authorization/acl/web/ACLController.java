package com.carbonldp.authorization.acl.web;

import com.carbonldp.ldp.web.AbstractLDPController;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = "/**/~acl/" )
public class ACLController extends AbstractLDPController {

	@RequestMapping( method = {RequestMethod.HEAD, RequestMethod.GET} )
	public ResponseEntity<Object> retrieve( HttpServletRequest request, HttpServletResponse response ) {
		// TODO: Implement
		throw new NotImplementedException();
	}
}
