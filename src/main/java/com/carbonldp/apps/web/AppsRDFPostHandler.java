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

			IRI requestSubject = getRequestSubject( requestModel );
			RDFResource requestResource = new RDFResource( requestModel, requestSubject );

			validateRequestResource( requestResource );

			String targetURI = getTargetURL( request );

			if ( hasGenericRequestURI( requestResource ) ) {
				IRI forgedURI = forgeUniqueURI( requestResource, targetURI, request );
				requestResource = renameResource( requestResource, forgedURI );
			} else {
				validateRequestResourceRelativeness( requestResource, targetURI );
			}

			// TODO: After ensuring uniqueness, move this back into the "else" right above
			checkRequestResourceAvailability( requestResource );

			IRI resourceContext = requestResource.getIRI();
			requestResource = addMissingContext( requestResource, resourceContext );

			App app = new App( requestResource.getBaseModel(), requestResource.getIRI() );

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
			if ( IRIUtil.hasFragment( ValueUtil.getIRI( subject ) ) ) {
				throw new BadRequestException( "The request resource cannot have a fragment in its IRI." );
			}
		}

		private RDFResource addMissingContext( RDFResource requestResource, IRI resourceContext ) {
			AbstractModel modifiedModel = ModelUtil.replaceContext( requestResource.getBaseModel(), null, resourceContext );
			return new RDFResource( modifiedModel, requestResource.getIRI() );
		}

		private void checkRequestResourceAvailability( RDFResource requestResource ) {
			if ( sourceWithURIExists( requestResource.getIRI() ) ) {
				throw new ConflictException( "The IRI is already in use." );
			}
		}

		private boolean sourceWithURIExists( IRI sourceURI ) {
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
