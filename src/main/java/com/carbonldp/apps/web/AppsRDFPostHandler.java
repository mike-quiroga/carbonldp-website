package com.carbonldp.apps.web;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

@RequestHandler
public class AppsRDFPostHandler extends AbstractRDFPostRequestHandler<App> {

	private final AppService appService;

	@Autowired
	public AppsRDFPostHandler( AppService appService ) {
		this.appService = appService;
	}

	@Override
	protected App getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return new App( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, App documentResourceView ) {
		appService.create( documentResourceView );
	}

	/*
		public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
			validateRequestModel( requestModel );

			URI requestSubject = getRequestSubject( requestModel );
			RDFResource requestResource = new RDFResource( requestModel, requestSubject );

			validateRequestResource( requestResource );

			String targetURI = getTargetURL( request );

			if ( hasGenericRequestURI( requestResource ) ) {
				URI forgedURI = forgeUniqueURI( requestResource, targetURI, request );
				requestResource = renameResource( requestResource, forgedURI );
			} else {
				validateRequestResourceRelativeness( requestResource, targetURI );
			}

			// TODO: After ensuring uniqueness, move this back into the "else" right above
			checkRequestResourceAvailability( requestResource );

			URI resourceContext = requestResource.getURI();
			requestResource = addMissingContext( requestResource, resourceContext );

			App app = new App( requestResource.getBaseModel(), requestResource.getURI() );

			appService.create( app );

			return new ResponseEntity<>( new EmptyResponse(), HttpStatus.CREATED );
		}

		@Override
		protected void validateRequestResourcesNumber( int size ) {
			super.validateRequestResourcesNumber( size );
			if ( size > 1 ) throw new BadRequestException( "The request cannot contain more than one rdf resource." );
		}

		@Override
		protected void validateRequestResource( Resource subject ) {
			super.validateRequestResource( subject );
			if ( URIUtil.hasFragment( ValueUtil.getURI( subject ) ) ) {
				throw new BadRequestException( "The request resource cannot have a fragment in its URI." );
			}
		}

		private RDFResource addMissingContext( RDFResource requestResource, URI resourceContext ) {
			AbstractModel modifiedModel = ModelUtil.replaceContext( requestResource.getBaseModel(), null, resourceContext );
			return new RDFResource( modifiedModel, requestResource.getURI() );
		}

		private void checkRequestResourceAvailability( RDFResource requestResource ) {
			if ( sourceWithURIExists( requestResource.getURI() ) ) {
				throw new ConflictException( "The URI is already in use." );
			}
		}

		private boolean sourceWithURIExists( URI sourceURI ) {
			return sourceService.exists( sourceURI );
		}

		private void validateRequestResource( RDFResource requestResource ) {
			List<Infraction> infractions = AppFactory.validateBasicContainer( requestResource, false );
			if ( ! infractions.isEmpty() ) {
				throw new BadRequestException( 0 );
			}
		}
	*/
}
