package com.carbonldp.jobs.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.jobs.Job;
import com.carbonldp.jobs.JobDescription;
import com.carbonldp.jobs.ManualExecution;
import com.carbonldp.jobs.TriggerService;
import com.carbonldp.ldp.containers.AddMembersAction;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class ManualExecutionPOSTHandler extends AbstractRDFPostRequestHandler<ManualExecution> {
	TriggerService triggerService;

	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) throw new NotFoundException();

		executeTrigger( targetURI );

		addTypeLinkHeader( APIPreferences.InteractionModel.TRIGGER );
		return createSuccessfulResponse( targetURI );

	}

	protected void executeTrigger( URI targetUri ) {triggerService.executeTrigger( targetUri ); }

	protected ResponseEntity<Object> createSuccessfulResponse( URI affectedResourceURI ) {
		DateTime modified = sourceService.getModified( affectedResourceURI );

		setETagHeader( modified );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	@Autowired
	public void setTriggerService( TriggerService triggerService ) {this.triggerService = triggerService; }

}
