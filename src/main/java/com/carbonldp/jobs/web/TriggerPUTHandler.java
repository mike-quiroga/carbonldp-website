package com.carbonldp.jobs.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class TriggerPUTHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) throw new NotFoundException();

		String contentType = request.getContentType();

		if ( contentType == null ) throw new BadRequestException( 0x4004 );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );

		switch ( interactionModel ) {
			case TRIGGER:
				return handleTrigger( targetURI );
			default:
				throw new BadRequestException( 0x4002 );
		}
	}
}
