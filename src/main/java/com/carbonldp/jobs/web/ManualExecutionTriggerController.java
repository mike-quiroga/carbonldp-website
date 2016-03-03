package com.carbonldp.jobs.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.web.AbstractController;
import com.carbonldp.web.config.InteractionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Controller
@RequestMapping( value = "/platform/apps/*/jobs/*/manual-executions/" )
public class ManualExecutionTriggerController extends AbstractController {
	private ManualExecutionPOSTHandler postRequestHandler;

	@InteractionModel( value = {APIPreferences.InteractionModel.TRIGGER}, handlesDefault = true )
	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> execute( HttpServletRequest request, HttpServletResponse response ) {
		return postRequestHandler.handleRequest( request, response );
	}

	@Autowired
	public void setPutRequestHandler( ManualExecutionPOSTHandler postRequestHandler ) {this.postRequestHandler = postRequestHandler;}

}
