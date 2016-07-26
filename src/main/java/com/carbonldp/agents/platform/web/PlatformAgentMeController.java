package com.carbonldp.agents.platform.web;

import com.carbonldp.agents.AgentMeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since _version_
 */
@Controller
@RequestMapping( value = "/platform/agents/me/" )
public class PlatformAgentMeController {
	private AgentMeHandler getHandler;

	@RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.OPTIONS} )
	public ResponseEntity<Object> getAgent( HttpServletRequest request, HttpServletResponse response ) {
		return getHandler.handleRequest( request, response );
	}

	@Autowired
	public void setGetHandler( AgentMeHandler getHandler ) {
		this.getHandler = getHandler;
	}
}
