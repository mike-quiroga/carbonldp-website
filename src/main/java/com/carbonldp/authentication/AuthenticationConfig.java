package com.carbonldp.authentication;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.roles.AppContextClearFilter;
import com.carbonldp.apps.roles.AppRolePersistenceFilter;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authentication.ticket.JWTicketAuthenticationEntryPoint;
import com.carbonldp.authentication.ticket.JWTicketAuthenticationFilter;
import com.carbonldp.authentication.token.JWTokenAuthenticationEntryPoint;
import com.carbonldp.authentication.token.JWTokenAuthenticationFilter;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.SecurityContextExchanger;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.web.cors.CORSAppContextFilter;
import com.carbonldp.web.cors.CORSPlatformContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableAspectJAutoProxy
public class AuthenticationConfig {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	@Qualifier( "platformAgentRepository" )
	private AgentRepository platformAgentRepository;
	@Autowired
	private PlatformRoleRepository platformRoleRepository;
	@Autowired
	private PlatformPrivilegeRepository platformPrivilegeRepository;
	@Autowired
	private AppRoleRepository appRoleRepository;
	@Autowired
	@Qualifier( "appAgentRepository" )
	private AgentRepository appAgentRepository;

	@Autowired
	public void configureGlobal( AuthenticationManagerBuilder auth ) {
		auth.authenticationProvider( platformAgentUsernamePasswordAuthenticationProvider() );
		auth.authenticationProvider( tokenAuthenticationProvider() );
		auth.authenticationProvider( appsAgentUsernamePasswordAuthenticationProvider() );
	}

	@Bean
	public AuthenticationProvider platformAgentUsernamePasswordAuthenticationProvider() {
		return new PlatformAgentUsernamePasswordAuthenticationProvider( platformAgentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Bean
	public AuthenticationProvider appsAgentUsernamePasswordAuthenticationProvider() {
		return new AppAgentUsernamePasswordAuthenticationProvider( appAgentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Bean
	public AuthenticationProvider tokenAuthenticationProvider() {
		return new IRIAuthenticationProvider( platformAgentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Bean
	public AppRolePersistenceFilter appRolePersistenceFilter() {
		return new AppRolePersistenceFilter( appRoleRepository );
	}

	@Bean
	public AppContextClearFilter platformAppRolePersistanceFilter() {
		return new AppContextClearFilter();
	}

	@Bean
	public BasicAuthenticationFilter basicAuthenticationFilter() {
		return new BasicAuthenticationFilter( authenticationManager, basicAuthenticationEntryPoint() );
	}

	@Bean
	public JWTokenAuthenticationFilter tokenAuthenticationFilter() {
		return new JWTokenAuthenticationFilter( authenticationManager, jwTokenAuthenticationEntryPoint() );
	}

	@Bean
	public JWTicketAuthenticationFilter ticketAuthenticationFilter() {
		return new JWTicketAuthenticationFilter( authenticationManager, jwTicketAuthenticationEntryPoint() );
	}

	@Bean
	public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
		BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName( configurationRepository.getRealmName() );
		return entryPoint;
	}

	@Bean
	public AuthenticationEntryPoint jwTokenAuthenticationEntryPoint() {
		return new JWTokenAuthenticationEntryPoint();
	}

	@Bean
	public AuthenticationEntryPoint jwTicketAuthenticationEntryPoint() {
		return new JWTicketAuthenticationEntryPoint();
	}

	@Bean
	public SecurityContextExchanger securityContextExchanger() {
		return new SecurityContextExchanger();
	}

	@Bean
	public CORSAppContextFilter corsAppContextFilter() {
		return new CORSAppContextFilter();
	}

	@Bean
	public CORSPlatformContextFilter corsPlatformContextFilter() {
		return new CORSPlatformContextFilter();
	}

	@Bean
	public AnonymousAuthenticationProvider anonymousAuthenticationProvider() {
		return new AnonymousAuthenticationProvider( platformRoleRepository, platformPrivilegeRepository );
	}

	@Bean
	public AnonymousAuthenticationFilter anonymousTokenExtenderFilter() {
		return new AnonymousAuthenticationFilter( anonymousAuthenticationProvider() );
	}
}
