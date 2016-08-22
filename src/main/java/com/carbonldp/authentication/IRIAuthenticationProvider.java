package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author NestorVenegas
 * @since 0.15.0-ALPHA
 */
public class IRIAuthenticationProvider extends AbstractSesameAuthenticationProvider {
	public IRIAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	private TransactionWrapper transactionWrapper;
	private AppRepository appRepository;
	private RDFSourceRepository sourceRepository;

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInPlatformContext
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		IRIAuthenticationToken iriAuthenticationToken = getIRIAuthenticationToken( authentication );
		IRI agentIRI = iriAuthenticationToken.getAgentIRI();
		IRI appRelatedIRI = iriAuthenticationToken.getAppRelatedIRI();
		Agent agent;
		App appRelated = null;
		validateCredentials( agentIRI );
		if ( appRelatedIRI == null ) agent = agentRepository.get( agentIRI );
		else {
			appRelated = appRepository.get( appRelatedIRI );
			agent = transactionWrapper.runWithSystemPermissionsInAppContext( appRelated, () -> new Agent( sourceRepository.get( agentIRI ) ) );
		}
		if ( agent == null || agent.getBaseModel().size() == 0 ) throw new BadCredentialsException( "Wrong credentials" );

		if ( ! agent.isEnabled() ) throw new BadCredentialsException( "Wrong credentials" );

		return createAgentAuthenticationToken( appRelated, agent );

	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return IRIAuthenticationToken.class.isAssignableFrom( authentication );
	}

	protected IRIAuthenticationToken getIRIAuthenticationToken( Authentication authentication ) {
		if ( ! ( authentication instanceof IRIAuthenticationToken ) ) throw new IllegalArgumentException( "Authentication is not instance of JWTAuthentication token" );
		return (IRIAuthenticationToken) authentication;
	}

	protected void validateCredentials( IRI agentIRI ) {
		if ( agentIRI.stringValue().trim().length() == 0 ) throw new BadCredentialsException( "Wrong credentials" );
	}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {
		this.transactionWrapper = transactionWrapper;
	}

	@Autowired
	public void setAppRepository( AppRepository appRepository ) {
		this.appRepository = appRepository;
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
