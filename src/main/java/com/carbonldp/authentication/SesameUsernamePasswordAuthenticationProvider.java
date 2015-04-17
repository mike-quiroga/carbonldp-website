package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.utils.AuthenticationUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.security.NoSuchAlgorithmException;

public abstract class SesameUsernamePasswordAuthenticationProvider extends AbstractSesameAuthenticationProvider {
	public SesameUsernamePasswordAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	protected String getUsername( Authentication authentication ) {
		Object rawPrincipal = authentication.getPrincipal();
		if ( ! ( rawPrincipal instanceof String ) ) throw new BadCredentialsException( "Wrong credentials" );
		return (String) rawPrincipal;
	}

	protected String getPassword( Authentication authentication ) {
		Object rawCredentials = authentication.getCredentials();
		if ( ! ( rawCredentials instanceof String ) ) throw new BadCredentialsException( "Wrong credentials" );
		return (String) rawCredentials;
	}

	protected void validateCredentials( String username, String password ) {
		if ( username.trim().length() == 0 || password.trim().length() == 0 ) throw new BadCredentialsException( "Wrong credentials" );
	}

	protected String getHashedPassword( String password, Agent agent ) {
		String salt = agent.getSalt();
		String saltedPassword = AuthenticationUtil.saltPassword( password, salt );
		String hashedPassword = null;

		try {
			hashedPassword = AuthenticationUtil.hashPassword( saltedPassword );
		} catch ( NoSuchAlgorithmException exception ) {
			throw new AuthenticationServiceException( "Password validation failed" );
		}

		return hashedPassword;
	}

	protected boolean passwordsMatch( String hashedPasword, Agent agent ) {
		return hashedPasword.equals( agent.getPassword() );
	}
}
