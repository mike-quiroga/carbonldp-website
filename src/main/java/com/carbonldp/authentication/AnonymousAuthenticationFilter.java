package com.carbonldp.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

public class AnonymousAuthenticationFilter extends GenericFilterBean implements Filter {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );

	private final AnonymousAuthenticationProvider anonymousAuthenticationProvider;

	public AnonymousAuthenticationFilter( AnonymousAuthenticationProvider anonymousAuthenticationProvider ) {
		this.anonymousAuthenticationProvider = anonymousAuthenticationProvider;
	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		if ( SecurityContextHolder.getContext().getAuthentication() == null ) {
			Authentication authentication = anonymousAuthenticationProvider.authenticate();
			SecurityContextHolder.getContext().setAuthentication( authentication );
			if ( LOG.isDebugEnabled() ) LOG.debug( "Authentication token set to: {}", authentication );
		}

		chain.doFilter( request, response );
	}
}
