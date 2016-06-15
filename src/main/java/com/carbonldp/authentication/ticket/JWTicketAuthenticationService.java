package com.carbonldp.authentication.ticket;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.Ticket;
import com.carbonldp.authentication.TicketFactory;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.jobs.ImportBackupJob;
import com.carbonldp.jobs.Job;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.web.exceptions.ForbiddenException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NestorVenegas
 * @since 0.36.0
 */
public class JWTicketAuthenticationService extends AbstractComponent implements TicketService {

	protected PermissionEvaluator permissionEvaluator;
	protected RDFSourceService sourceService;

	@Override
	public Ticket createTicket( IRI targetIRI ) {
		checkPermissionsOverTheTargetIRI( targetIRI );
		Date expTime = new Date( System.currentTimeMillis() + Vars.getInstance().getTicketExpirationTime() );
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new StupidityException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		String agentTokenString = agentToken.getAgent().getSubject().stringValue();

		return TicketFactory.getInstance().create( agentTokenString, expTime, signatureAlgorithm, targetIRI );
	}

	private void checkPermissionsOverTheTargetIRI( IRI targetIRI ) {
		if ( ! sourceService.exists( targetIRI ) ) throw new InvalidResourceException( new Infraction( 0x2011, "iri", targetIRI.stringValue() ) );
		if ( ! permissionEvaluator.hasPermission( SecurityContextHolder.getContext().getAuthentication(), targetIRI, ACEDescription.Permission.READ ) ) {
			Map<String, String> errorMessage = new HashMap<>();
			errorMessage.put( "action", "create ticket" );
			errorMessage.put( "uri", targetIRI.stringValue() );
			throw new ForbiddenException( new Infraction( 0x7001, errorMessage ) );
		}
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
