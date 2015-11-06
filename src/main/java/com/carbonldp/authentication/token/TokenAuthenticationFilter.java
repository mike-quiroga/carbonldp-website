package com.carbonldp.authentication.token;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class TokenAuthenticationFilter extends GenericFilterBean {
	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
//		if ( SecurityContextHolder.getContext().getAuthentication() == null ) {
//			Authentication authentication = anonymousAuthenticationProvider.authenticate();
//			SecurityContextHolder.getContext().setAuthentication( authentication );
//			if ( LOG.isDebugEnabled() ) LOG.debug( "Authentication token set to: {}", authentication );
//		}
//		chain.doFilter( request, response );
	}
}
