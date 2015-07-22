package com.carbonldp.web.cors;

import com.carbonldp.web.AbstractUniqueFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CORSContextFilter extends AbstractUniqueFilter {
	public CORSContextFilter( String filterAppliedFlag ) {
		super( filterAppliedFlag );
	}

	@Override
	protected void applyFilter( HttpServletRequest request, HttpServletResponse response ) {
		String origin = request.getHeader( "Origin" );
		if ( origin == null ) return;

		if ( ! isOriginAllowed( origin ) ) return;

		response.addHeader( "Access-Control-Allow-Credentials", "true" );
		
		response.addHeader( "Access-Control-Allow-Origin", origin );
		if ( isPreflightRequest( request ) ) addPreflightHeaders( request, response );
	}

	protected boolean isOriginAllowed( String origin ) {
		return true;
	}

	private boolean isPreflightRequest( HttpServletRequest request ) {
		return request.getHeader( "Access-Control-Request-Method" ) != null;
	}

	private void addPreflightHeaders( HttpServletRequest request, HttpServletResponse response ) {
		response.addHeader( "Access-Control-Allow-Headers", request.getHeader( "Access-Control-Request-Headers" ) );
		response.addHeader( "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS" );
	}
}
