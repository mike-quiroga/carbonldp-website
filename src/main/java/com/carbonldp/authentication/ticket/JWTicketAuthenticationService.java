package com.carbonldp.authentication.ticket;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.Ticket;
import com.carbonldp.authentication.TicketFactory;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.utils.IRIUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.openrdf.model.IRI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author NestorVenegas
 * @since 0.36.0
 */
public class JWTicketAuthenticationService extends AbstractComponent implements TicketService {

	private String containerSlug;

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

	@Override
	public IRI getTicketsContainerIRI() {
		IRI appIRI = AppContextHolder.getContext().getApplication().getRootContainerIRI();
		if ( appIRI == null ) throw new RuntimeException( "app agent repository should be running in App context" );
		return getContainerIRI( appIRI );
	}

	private IRI getContainerIRI( IRI rootContainerIRI ) {
		return IRIUtil.createChildIRI( rootContainerIRI, containerSlug );
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
