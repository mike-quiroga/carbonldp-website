package com.carbonldp.apps.context;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.web.AbstractUniqueFilter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.carbonldp.Consts.*;

public class AppContextPersistenceFilter extends AbstractUniqueFilter {
	public static final String FILTER_APPLIED = "__carbon_acpf_applied";

	private final AppContextRepository appContextRepository;

	public AppContextPersistenceFilter( AppContextRepository appContextRepository ) {
		super( FILTER_APPLIED );
		this.appContextRepository = appContextRepository;
	}

	@Override
	protected void applyFilter( HttpServletRequest request, HttpServletResponse response ) {
		IRI rootContainerIRI = getRootContainerIRI( request, response );

		if ( rootContainerIRI == null ) {
			// The IRI doesn't match an App's Root Container IRI
			// TODO: Add more information
			request.removeAttribute( FILTER_APPLIED );
			response.setStatus( HttpStatus.NOT_FOUND.value() );
			return;
		}

		App app = appContextRepository.getApp( rootContainerIRI );

		if ( app == null ) {
			// TODO: Add more information
			request.removeAttribute( FILTER_APPLIED );
			response.setStatus( HttpStatus.NOT_FOUND.value() );
			throw new ResourceDoesntExistException();
		}

		AppContext context = AppContextHolder.createEmptyContext();
		context.setApplication( app );
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "AppContext set to: '{}'", app.toString() );
		}
	}

	@Override
	protected void cleanFilter() {
		AppContextHolder.clearContext();
		if ( LOG.isDebugEnabled() ) LOG.debug( "AppContext now cleared, as request processing completed" );
	}

	private IRI getRootContainerIRI( HttpServletRequest httpRequest, HttpServletResponse httpResponse ) {
		String requestIRI = httpRequest.getRequestURI();
		requestIRI = requestIRI.startsWith( SLASH ) ? requestIRI.substring( 1 ) : requestIRI;

		requestIRI = requestIRI.startsWith( Vars.getInstance().getMainContainer() ) ? requestIRI.substring( Vars.getInstance().getMainContainer().length() ) : requestIRI;

		if ( ! requestIRI.startsWith( Vars.getInstance().getAppsEntryPoint() ) ) return null;

		requestIRI = requestIRI.replace( Vars.getInstance().getAppsEntryPoint(), EMPTY_STRING );

		if ( requestIRI.isEmpty() ) return null;

		String applicationSlug;
		int slashIndex = requestIRI.indexOf( SLASH );
		if ( slashIndex == - 1 ) applicationSlug = requestIRI;
		else applicationSlug = requestIRI.substring( 0, slashIndex );

		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( Vars.getInstance().getAppsEntryPointURL() ).append( applicationSlug ).append( SLASH );
		return SimpleValueFactory.getInstance().createIRI( uriBuilder.toString() );
	}

}
