package com.carbonldp.apps.web;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;
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
	protected void createChild( IRI targetIRI, App documentResourceView ) {
		appService.create( documentResourceView );
	}

	/*
		public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
			validateRequestModel( requestModel );

			IRI requestSubject = getRequestSubject( requestModel );
			RDFResource requestResource = new RDFResource( requestModel, requestSubject );

			validateRequestResource( requestResource );

			String targetIRI = getTargetURL( request );

			if ( hasGenericRequestIRI( requestResource ) ) {
				IRI forgedIRI = forgeUniqueIRI( requestResource, targetIRI, request );
				requestResource = renameResource( requestResource, forgedIRI );
			} else {
				validateRequestResourceRelativeness( requestResource, targetIRI );
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
			if ( sourceWithIRIExists( requestResource.getIRI() ) ) {
				throw new ConflictException( "The IRI is already in use." );
			}
		}

		private boolean sourceWithIRIExists( IRI sourceIRI ) {
			return sourceService.exists( sourceIRI );
		}

		private void validateRequestResource( RDFResource requestResource ) {
			List<Infraction> infractions = AppFactory.validateBasicContainer( requestResource, false );
			if ( ! infractions.isEmpty() ) {
				throw new BadRequestException( 0 );
			}
		}
	*/
}
