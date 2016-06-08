package com.carbonldp.authentication.ticket;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.Ticket;
import com.carbonldp.authentication.TicketFactory;
import com.carbonldp.exceptions.StupidityException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.openrdf.model.IRI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class JWTicketAuthenticationService extends AbstractComponent implements TicketService {

	@Override
	public Ticket createTicket( IRI targetIRI ) {
		Date expTime = new Date( System.currentTimeMillis() + Vars.getInstance().getTicketExpirationTime() );
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new StupidityException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		String agentTokenString = agentToken.getAgent().getSubject().stringValue();

		return TicketFactory.getInstance().create( agentTokenString, expTime, signatureAlgorithm, targetIRI );
	}
}
