package com.carbonldp.authentication.ticket;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.authentication.*;
import com.carbonldp.authentication.token.JWTUtil;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import io.jsonwebtoken.SignatureAlgorithm;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author NestorVenegas
 * @since 0.36.0
 */
public class JWTicketAuthenticationService extends AbstractComponent implements TicketService {

	protected PermissionEvaluator permissionEvaluator;
	protected RDFSourceService sourceService;

	@Override
	public Ticket createTicket( IRI targetIRI ) {
		if ( ! sourceService.exists( targetIRI ) ) throw new InvalidResourceException( new Infraction( 0x2011, "iri", targetIRI.stringValue() ) );
		Date expTime = new Date( System.currentTimeMillis() + Vars.getInstance().getTicketExpirationTime() );
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new StupidityException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		String agentTokenString = agentToken.getAgent().getSubject().stringValue();
		App appRelated = agentToken.getApp();

		String jwt;
		Ticket ticket;

		if ( appRelated == null ) {
			jwt = JWTUtil.encodeTicket( agentTokenString, expTime, targetIRI );
			ticket = TicketFactory.getInstance().getRDFTicket( null, jwt, expTime, targetIRI );
		} else {
			jwt = JWTUtil.encodeTicket( appRelated.getIRI(), agentTokenString, expTime, targetIRI );
			ticket = TicketFactory.getInstance().getRDFTicket( appRelated.getIRI(), jwt, expTime, targetIRI );
		}

		return ticket;
	}

	@Autowired
	public void setPermissionEvaluator( PermissionEvaluator permissionEvaluator ) {
		this.permissionEvaluator = permissionEvaluator;
	}

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}
}
