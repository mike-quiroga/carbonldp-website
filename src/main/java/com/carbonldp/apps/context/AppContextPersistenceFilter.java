package com.carbonldp.apps.context;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.web.AbstractUniqueFilter;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

public class AppContextPersistenceFilter extends AbstractUniqueFilter {
	public static final String FILTER_APPLIED = "__carbon_acpf_applied";

	private final AppContextRepository appContextRepository;

	public AppContextPersistenceFilter( AppContextRepository appContextRepository ) {
		super( FILTER_APPLIED );
		this.appContextRepository = appContextRepository;
	}

	@Override
	protected void applyFilter( HttpServletRequest request, HttpServletResponse response ) {
		URI rootContainerURI = getRootContainerURI( request, response );

		if ( rootContainerURI == null ) {
			// The URI doesn't match an App's Root Container URI
			// TODO: Add more information
			request.removeAttribute( FILTER_APPLIED );
			response.setStatus( HttpStatus.NOT_FOUND.value() );
			return;
		}

		App app = appContextRepository.getApp( rootContainerURI );

		if ( app == null ) {
			// TODO: Add more information
			request.removeAttribute( FILTER_APPLIED );
			response.setStatus( HttpStatus.NOT_FOUND.value() );
			return;
		}

		AppContext context = AppContextHolder.createEmptyContext();
		context.setApplication( app );
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "AppContext set to: '{}'", app );
		}
	}

	@Override
	protected void cleanFilter() {
		AppContextHolder.clearContext();
		if ( LOG.isDebugEnabled() ) LOG.debug( "AppContext now cleared, as request processing completed" );
	}

	private URI getRootContainerURI( HttpServletRequest httpRequest, HttpServletResponse httpResponse ) {
		String requestURI = httpRequest.getRequestURI();
		requestURI = requestURI.startsWith( SLASH ) ? requestURI.substring( 1 ) : requestURI;

		requestURI = requestURI.startsWith( Vars.getInstance().getMainContainer() ) ? requestURI.substring( Vars.getInstance().getMainContainer().length() ) : requestURI;

		if ( ! requestURI.startsWith( Vars.getInstance().getAppsEntryPoint() ) ) return null;

		requestURI = requestURI.replace( Vars.getInstance().getAppsEntryPoint(), EMPTY_STRING );

		if ( requestURI.isEmpty() ) return null;

		String applicationSlug;
		int slashIndex = requestURI.indexOf( SLASH );
		if ( slashIndex == - 1 ) applicationSlug = requestURI;
		else applicationSlug = requestURI.substring( 0, slashIndex );

		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( Vars.getInstance().getAppsEntryPointURL() ).append( applicationSlug ).append( SLASH );
		return new URIImpl( uriBuilder.toString() );
	}

}
