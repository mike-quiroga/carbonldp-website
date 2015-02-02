package com.carbonldp.apps.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.carbonldp.apps.web.handlers.ApplicationsPOSTRequestHandler;
import com.carbonldp.web.AbstractController;

@Controller
@RequestMapping(value = "/apps/")
public class ApplicationsController extends AbstractController {
	@Autowired
	private ApplicationsPOSTRequestHandler postRequestHandler;

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> createApplication(HttpServletRequest request, HttpServletResponse response) {
		return postRequestHandler.handleRequest(request, response);
	}
}
