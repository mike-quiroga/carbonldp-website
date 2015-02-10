package com.carbonldp.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.carbonldp.ConfigurationRepository;
import com.carbonldp.agents.AgentService;
import com.carbonldp.authorization.PlatformRoleService;
import com.carbonldp.authorization.SecurityContextExchanger;

@Configuration
@EnableAspectJAutoProxy
public class AuthenticationConfig {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private AgentService agentService;
	@Autowired
	private PlatformRoleService platformRoleService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(sesameUsernamePasswordAuthenticationProvider());
	}

	@Bean
	public AuthenticationProvider sesameUsernamePasswordAuthenticationProvider() {
		return new SesameUsernamePasswordAuthenticationProvider(agentService, platformRoleService);
	}

	@Bean
	public BasicAuthenticationFilter basicAuthenticationFilter() {
		return new BasicAuthenticationFilter(authenticationManager, basicAuthenticationEntryPoint());
	}

	@Bean
	public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
		BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName(configurationRepository.getRealmName());
		return entryPoint;
	}

	@Bean
	public SecurityContextExchanger securityContexyExchanger() {
		return new SecurityContextExchanger();
	}
}
