package com.carbonldp.apps.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.carbonldp.apps.web.handlers.AppsPOSTRequestHandler;
import com.carbonldp.web.AbstractController;

@Controller
@RequestMapping(value = "/platform/apps/")
public class AppsController extends AbstractController {
	@Autowired
	private AppsPOSTRequestHandler postRequestHandler;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> createApplication(Model requestModel, HttpServletRequest request, HttpServletResponse response) {
		return postRequestHandler.handleRequest(request, response);
	}
}
