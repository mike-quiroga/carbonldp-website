package com.carbonldp.authentication.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.authentication.AbstractSesameAuthenticationProvider;
import com.carbonldp.authentication.IRIAuthenticationToken;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class AppIRIAuthenticationProvider extends AbstractSesameAuthenticationProvider {
	public AppIRIAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInAppContext
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		IRI agentIRI = getAgentIRI( authentication );
		validateCredentials( agentIRI );

		Agent agent = agentRepository.get( agentIRI );
		if ( agent == null || agent.getBaseModel().size() == 0 ) throw new BadCredentialsException( "Wrong credentials" );

		if ( ! agent.isEnabled() ) throw new BadCredentialsException( "Wrong credentials" );

		return createAgentAuthenticationToken( agent );

	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return IRIAuthenticationToken.class.isAssignableFrom( authentication );
	}

	protected IRI getAgentIRI( Authentication authentication ) {
		if ( ! ( authentication instanceof IRIAuthenticationToken ) ) throw new IllegalArgumentException( "Authentication is not instance of JWTAuthentication token" );
		IRIAuthenticationToken authenticationToken = (IRIAuthenticationToken) authentication;

		return authenticationToken.getAgentIRI();
	}

	protected void validateCredentials( IRI agentIRI ) {
		if ( agentIRI.stringValue().trim().length() == 0 ) throw new BadCredentialsException( "Wrong credentials" );
	}
}
