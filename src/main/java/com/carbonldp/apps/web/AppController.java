package com.carbonldp.apps.web;

import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.AbstractController;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = "/platform/apps/?*/" )
public class AppController extends AbstractController {
	private AppRDFPutToRDFSourceHandler putHandler;
	private AppPATCHHandler patchHandler;
	private AppDELETEHandler deleteHandler;

	@RequestMapping( method = RequestMethod.PUT )
	public ResponseEntity<Object> replaceApp( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return putHandler.handleRequest( requestDocument, request, response );
	}

	@RequestMapping( method = RequestMethod.PATCH )
	public ResponseEntity<Object> patchApp( @RequestBody AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		return patchHandler.handleRequest( requestModel, request, response );
	}

	@RequestMapping( method = RequestMethod.DELETE )
	public ResponseEntity<Object> deleteApp( @RequestBody( required = false ) RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return deleteHandler.handleRequest( requestDocument, request, response );

	}

	@Autowired
	public void setPUTHandler( AppRDFPutToRDFSourceHandler putHandler ) {
		this.putHandler = putHandler;
	}

	@Autowired
	public void setPATCHHandler( AppPATCHHandler patchHandler ) {
		this.patchHandler = patchHandler;
	}

	@Autowired
	public void setDELETEHandler( AppDELETEHandler deleteHandler ) {
		this.deleteHandler = deleteHandler;
	}
}
