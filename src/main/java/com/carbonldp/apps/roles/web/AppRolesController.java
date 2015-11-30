package com.carbonldp.apps.roles.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.config.InteractionModel;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = "apps/*/roles/" )
public class AppRolesController {
	@RequestMapping( method = RequestMethod.POST )
	public void createAppRole() {
		// TODO: Implement it
		throw new NotImplementedException();
	}

	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	@RequestMapping( method = RequestMethod.PUT )
	public void defineParentChildRelation( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {

	}
}
