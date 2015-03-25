package com.carbonldp.apps.resources;

import com.carbonldp.ldp.web.AbstractLDPController;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = {"/apps/?*/", "/apps/?*/**"} )
public class AppResourceController extends AbstractLDPController {

	private static final String FILE_PARAMETER = "file";
	private static final String FILE_NAME_PARAMETER = "name";

	@Autowired
	private AppResourceGETHandler getRDFHandler;

	@Autowired
	private AppResourcePOSTHandler postRDFHandler;
	@Autowired
	private AppResourcePOSTNonRDFHandler postNonRDFHandler;
	@Autowired
	private AppResourcePUTHandler putHandler;
	@Autowired
	private AppResourceDELETEHandler deleteHandler;

	@RequestMapping( method = RequestMethod.HEAD )
	public ResponseEntity<Object> handleHEAD( HttpServletRequest request, HttpServletResponse response ) {
		return getRDFHandler.handleRequest( request, response );
	}

	@RequestMapping( method = RequestMethod.GET )
	public ResponseEntity<Object> handleGET( HttpServletRequest request, HttpServletResponse response ) {
		return getRDFHandler.handleRequest( request, response );
	}

	//@formatter:off
	@RequestMapping( method = RequestMethod.POST, consumes = {
			"application/ld+json",
			"text/turtle"
	} )
	//@formatter:on
	public ResponseEntity<Object> handleRDFPost( @RequestBody AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		return postRDFHandler.handleRequest( requestModel, request, response );
	}

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> handleNonRDFPost( @RequestBody AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		// TODO: Implement
		return new ResponseEntity<Object>( HttpStatus.NOT_IMPLEMENTED );
	}

	@RequestMapping( method = RequestMethod.POST, consumes = "multipart/form-data" )
	//@formatter:off
	public ResponseEntity<Object> handleMultipartPost(
			@RequestParam( value = FILE_NAME_PARAMETER, required = false ) String fileName,
			@RequestParam( value = FILE_PARAMETER, required = false ) MultipartFile file,
			HttpServletRequest request,
			HttpServletResponse response
			//@formatter:on
	) {
		// TODO: Implement
		return new ResponseEntity<Object>( HttpStatus.NOT_IMPLEMENTED );
	}

	@RequestMapping( method = RequestMethod.PUT )
	public ResponseEntity<Object> handlePUT( @RequestBody AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		return putHandler.handleRequest( requestModel, request, response );
	}

	@RequestMapping( method = RequestMethod.PATCH )
	public ResponseEntity<Object> handlePATCH( HttpServletRequest request, HttpServletResponse response ) {
		// TODO: Implement
		return new ResponseEntity<Object>( HttpStatus.NOT_IMPLEMENTED );
	}

	@RequestMapping( method = RequestMethod.DELETE )
	public ResponseEntity<Object> handleDELETE( HttpServletRequest request, HttpServletResponse response ) {
		return deleteHandler.handleRequest( request, response );
	}

}
