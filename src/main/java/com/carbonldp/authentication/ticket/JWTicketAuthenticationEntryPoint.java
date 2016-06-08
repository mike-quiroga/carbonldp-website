package com.carbonldp.authentication.ticket;

import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class JWTicketAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException, ServletException {
		response.setStatus( HttpStatus.SC_UNAUTHORIZED );
	}
}
