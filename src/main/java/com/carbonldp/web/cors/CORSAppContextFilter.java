package com.carbonldp.web.cors;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextRepository;

import java.util.Set;

public class CORSAppContextFilter extends CORSContextFilter {
	public static final String FILTER_APPLIED = "__carbon_cacf_applied";

	private AppContextRepository appContextRepository;

	//TODO use the right parameters
	public CORSAppContextFilter() {
		super( FILTER_APPLIED );
	}

	@Override
	protected boolean isOriginAllowed( String origin ) {
		Set<String> allowedOrigins = getAllowedOrigins();
		if ( allowedOrigins.isEmpty() ) return false;
		return isOriginAllowed( origin, allowedOrigins );
	}

	private Set<String> getAllowedOrigins() {
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) throw new IllegalStateException( "The filter needs to execute inside of an appContext." );
		App app = appContext.getApplication();
		return app.getDomains();
	}

	private boolean isOriginAllowed( String origin, Set<String> allowedOrigins ) {
		for ( String allowedOrigin : allowedOrigins ) {
			// TODO: Use patterns instead of plain domains
			if ( allowedOrigin.equals( origin ) ) return true;
		}
		return false;
	}

}
