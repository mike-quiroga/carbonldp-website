package com.carbonldp.agents.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = "/platform/agents/*/~validator-*/" )
public class AgentValidatorController {

	@Autowired
	private AgentValidatorGETHandler getHandler;

	@RequestMapping( method = RequestMethod.GET )
	public ResponseEntity<Object> triggerValidation( HttpServletRequest request, HttpServletResponse response ) {
		return getHandler.handleRequest( request, response );
	}
}
