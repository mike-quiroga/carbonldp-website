package com.carbonldp.ldp.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GETController {

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.GET)
	public ResponseEntity<Object> handleGET(@PathVariable("application") String applicationIdentifier, HttpServletRequest request, HttpServletResponse response) {
		return new ResponseEntity<Object>(HttpStatus.I_AM_A_TEAPOT);
	}
}
