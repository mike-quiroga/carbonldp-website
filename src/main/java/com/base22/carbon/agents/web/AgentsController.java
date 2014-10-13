package com.base22.carbon.agents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/agents")
public class AgentsController {

	@Autowired
	private AgentsRequestHandler requestHandler;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> registerAgent(@RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {
		try {
			return requestHandler.handleAgentRegistration(requestModel, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}
