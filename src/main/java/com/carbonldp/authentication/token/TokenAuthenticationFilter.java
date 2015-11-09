package com.carbonldp.authentication.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class TokenAuthenticationFilter extends GenericFilterBean implements Filter {
	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		Authentication authentication;
		authentication = SecurityContextHolder.getContext().getAuthentication();

		if ( SecurityContextHolder.getContext().getAuthentication() == null ) {

			System.out.println();
		}
		chain.doFilter( request, response );
	}
}
