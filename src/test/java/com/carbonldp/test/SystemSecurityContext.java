package com.carbonldp.test;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SystemSecurityContext {

	@Autowired
	protected AgentRepository agentRepository;

	@Autowired
	protected AuthenticationProvider sesameUsernamePasswordAuthenticationProvider;

	protected AppService appService;

	public SystemSecurityContext( AppService appService ) {
		this.appService = appService;
	}

	public void setAdminContext() {
		Authentication authentication = Mockito.mock( Authentication.class );

		Mockito.when( authentication.getPrincipal() ).thenReturn( "admin@carbonldp.com" );
		Mockito.when( authentication.getCredentials() ).thenReturn( "hello" );

		Agent agent = agentRepository.findByEmail( "admin@carbonldp.com" );
		Authentication token = sesameUsernamePasswordAuthenticationProvider.authenticate( authentication );
		SecurityContextHolder.getContext().setAuthentication( token );
	}

	public void createApp( App app ) {
		app = appService.create( app );
	}
}
