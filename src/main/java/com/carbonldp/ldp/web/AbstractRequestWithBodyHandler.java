package com.carbonldp.ldp.web;

import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRequestWithBodyHandler<E extends RDFResource> extends AbstractLDPRequestHandler {
	// TODO: delete validations that are no longer in use.
	protected boolean hasGenericRequestIRI( RDFResource resource ) {
		return configurationRepository.isGenericRequest( resource.getIRI().stringValue() );
	}

	protected Set<RDFResource> getRequestDocumentResources( AbstractModel requestModel ) {
		Set<RDFResource> documentResources = new HashSet<>();
		requestModel.subjects()
					.stream()
					.filter( ValueUtil::isIRI )
					.map( ValueUtil::getIRI )
					.filter( uri -> ! IRIUtil.hasFragment( uri ) )
					.map( uri -> new RDFResource( requestModel, uri ) )
					.forEach( documentResources::add )
		;
		return documentResources;
	}

	protected RDFResource getRequestDocumentResource( AbstractModel requestModel ) {
		RDFResource documentResource = null;
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isIRI( subject ) ) continue;
			IRI subjectIRI = ValueUtil.getIRI( subject );
			if ( IRIUtil.hasFragment( subjectIRI ) ) continue;
			if ( documentResource != null )
				throw new BadRequestException( "The request contains more than one document resource." );
			documentResource = new RDFResource( requestModel, subjectIRI );
		}
		return documentResource;
	}

	protected void validateRequestModel( AbstractModel requestModel ) {
		Set<Resource> subjects = requestModel.subjects();
		validateRequestResourcesNumber( subjects.size() );

//		for ( Resource subject : subjects )
//			validateRequestResource( subject );
	}

	protected void validateRequestResourcesNumber( int number ) {
		if ( number == 0 ) throw new BadRequestException( new Infraction( 0x2001, "rdf.type", "" ) );
	}

	protected void validateRequestResource( Resource subject ) {
		if ( ValueUtil.isBNode( subject ) ) throw new BadRequestException( "Blank nodes are not supported." );
	}

	protected Set<E> processDocumentResources( Set<RDFResource> requestDocumentResources, ResourceProcessor<E> resourceProcessor ) {
		validateRequestDocumentResourcesNumber( requestDocumentResources.size() );

		Set<E> processedResources = new HashSet<E>();
		for ( RDFResource documentResource : requestDocumentResources ) {
			processedResources.add( resourceProcessor.processResource( documentResource ) );
		}
		return processedResources;
	}

	protected <E extends RDFResource> E processDocumentResource( RDFResource requestDocumentResource, ResourceProcessor<E> resourceProcessor ) {
		if ( requestDocumentResource == null ) handleRequestWithNoDocumentResource();
		return resourceProcessor.processResource( requestDocumentResource );
	}

	protected void validateDocumentResource( IRI targetIRI, RDFResource requestDocumentResource ) {
		if ( requestDocumentResource == null ) handleRequestWithNoDocumentResource();
	}

	protected void handleRequestWithNoDocumentResource() {
		throw new BadRequestException( "The request doesn't contain a document resource." );
	}

	protected void validateRequestDocumentResourcesNumber( int number ) {
		if ( number == 0 ) throw new BadRequestException( "The request doesn't contain any resources " );
	}

	protected void seekForOrphanFragments( AbstractModel requestModel, RDFResource requestDocumentResource ) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isIRI( subject ) ) continue;
			IRI subjectIRI = ValueUtil.getIRI( subject );
			if ( ! IRIUtil.hasFragment( subjectIRI ) ) continue;
			IRI documentIRI = SimpleValueFactory.getInstance().createIRI( IRIUtil.getDocumentIRI( subjectIRI.stringValue() ) );
			if ( ! requestDocumentResource.getIRI().equals( documentIRI ) ) {
				throw new BadRequestException( "The request contains orphan fragments." );
			}
		}
	}

	protected void seekForOrphanFragments( AbstractModel requestModel, Set<RDFResource> requestDocumentResources ) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isIRI( subject ) ) continue;
			IRI subjectIRI = ValueUtil.getIRI( subject );
			if ( ! IRIUtil.hasFragment( subjectIRI ) ) continue;
			IRI documentIRI = SimpleValueFactory.getInstance().createIRI( IRIUtil.getDocumentIRI( subjectIRI.stringValue() ) );
			RDFResource fragmentResource = new RDFResource( requestModel, documentIRI );
			if ( ! requestDocumentResources.contains( fragmentResource ) ) {
				throw new BadRequestException( "All fragment resources must be accompanied by their document resource" );
			}
		}
	}

	@FunctionalInterface
	protected interface ResourceProcessor<J> {
		public J processResource( RDFResource resource );
	}
}
