package com.carbonldp.authentication.LDAP;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.SesameUsernamePasswordAuthenticationProvider;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class LDAPAuthenticationProvider extends SesameUsernamePasswordAuthenticationProvider {

	public LDAPAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		if ( AppContextHolder.getContext().isEmpty() ) return null;
		String username = getUsername( authentication );
		String password = getPassword( authentication );

		Set<LDAPAgent> agents = agentRepository.findByUID( username );
		if ( agents.isEmpty() ) throw new BadCredentialsException( "Wrong credentials" );
		return null;
//		for ( LDAPAgent agent : agents ) {
//			if(authenticate(agent))return
//		}
//		throw new BadCredentialsException( "Wrong credentials" );
	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return false;
	}
}
