package com.carbonldp.ldp.nonrdf;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.web.config.InteractionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
@Controller
@InteractionModel( APIPreferences.InteractionModel.NON_RDF_SOURCE )
@RequestMapping( "/**" )
public class NonRDFInteractionModelController {

	private BaseNonRDFPutRequestHandler putNonRDFHandler;

	@RequestMapping( method = RequestMethod.PUT )
	public ResponseEntity<Object> handleNonRDFPUT( InputStream bodyInputStream, HttpServletRequest request, HttpServletResponse response ) {
		return putNonRDFHandler.handleRequest( bodyInputStream, request, response );
	}

	@Autowired
	public void setPutNonRDFHandler( BaseNonRDFPutRequestHandler putNonRDFHandler ) {this.putNonRDFHandler = putNonRDFHandler;}
}
