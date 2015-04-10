package com.carbonldp.authentication;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.roles.AppRolePersistenceFilter;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.SecurityContextExchanger;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.web.cors.CORSAppContextFilter;
import com.carbonldp.web.cors.CORSPlatformContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
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
	private AgentRepository agentRepository;
	@Autowired
	private PlatformRoleRepository platformRoleRepository;
	@Autowired
	private PlatformPrivilegeRepository platformPrivilegeRepository;
	@Autowired
	private AppRoleRepository appRoleRepository;

	@Autowired
	public void configureGlobal( AuthenticationManagerBuilder auth ) {
		auth.authenticationProvider( sesameUsernamePasswordAuthenticationProvider() );
	}

	@Bean
	public AuthenticationProvider sesameUsernamePasswordAuthenticationProvider() {
		return new SesameUsernamePasswordAuthenticationProvider( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Bean
	public AppRolePersistenceFilter appRolePersistenceFilter() {
		return new AppRolePersistenceFilter( appRoleRepository );
	}

	@Bean
	public BasicAuthenticationFilter basicAuthenticationFilter() {
		return new BasicAuthenticationFilter( authenticationManager, basicAuthenticationEntryPoint() );
	}

	@Bean
	public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
		BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName( configurationRepository.getRealmName() );
		return entryPoint;
	}

	@Bean
	public SecurityContextExchanger securityContexyExchanger() {
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
}
