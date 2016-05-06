package com.carbonldp.authentication;

import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class BasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private String realmName;

	@Override
	public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException, ServletException {

		if ( ! isProgrammaticRequest( request ) ) response.addHeader( "WWW-Authenticate", "Basic realm=\"" + realmName + "\"" );
		response.setStatus( HttpStatus.SC_UNAUTHORIZED );
	}

	private boolean isProgrammaticRequest( HttpServletRequest request ) {
		boolean foundTextHTML = false;
		boolean foundMatchAll = false;

		Enumeration<String> acceptHeaders = request.getHeaders( "Accept" );
		while ( acceptHeaders.hasMoreElements() ) {
			String header = acceptHeaders.nextElement();
			for ( String acceptHeader : header.split( "," ) ) {
				String mimeType = acceptHeader.split( ";" )[0].trim();
				if ( mimeType.toLowerCase().equals( "text/html" ) ) foundTextHTML = true;
				if ( mimeType.toLowerCase().equals( "*/*" ) ) foundMatchAll = true;
			}
		}

		return ! ( foundTextHTML && foundMatchAll );
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName( String realmName ) {
		this.realmName = realmName;
	}
}
