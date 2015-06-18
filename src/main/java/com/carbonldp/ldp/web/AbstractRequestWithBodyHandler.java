package com.carbonldp.ldp.web;

import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.URIImpl;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRequestWithBodyHandler<E extends RDFResource> extends AbstractLDPRequestHandler {

	protected boolean hasGenericRequestURI( RDFResource resource ) {
		return configurationRepository.isGenericRequest( resource.getURI().stringValue() );
	}

	protected Set<RDFResource> getRequestDocumentResources( AbstractModel requestModel ) {
		Set<RDFResource> documentResources = new HashSet<>();
		requestModel.subjects()
					.stream()
					.filter( ValueUtil::isURI )
					.map( ValueUtil::getURI )
					.filter( uri -> ! URIUtil.hasFragment( uri ) )
					.map( uri -> new RDFResource( requestModel, uri ) )
					.forEach( documentResources::add )
		;
		return documentResources;
	}

	protected RDFResource getRequestDocumentResource( AbstractModel requestModel ) {
		RDFResource documentResource = null;
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( URIUtil.hasFragment( subjectURI ) ) continue;
			if ( documentResource != null )
				throw new BadRequestException( "The request contains more than one document resource." );
			documentResource = new RDFResource( requestModel, subjectURI );
		}
		return documentResource;
	}

	protected void validateRequestModel( AbstractModel requestModel ) {
		Set<Resource> subjects = requestModel.subjects();
		validateRequestResourcesNumber( subjects.size() );

		for ( Resource subject : subjects )
			validateRequestResource( subject );
	}

	protected void validateRequestResourcesNumber( int number ) {
		if ( number == 0 ) throw new BadRequestException( "The request doesn't contain rdf resources." );
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

	protected void validateDocumentResource( URI targetURI, RDFResource requestDocumentResource ) {
		if ( requestDocumentResource == null ) handleRequestWithNoDocumentResource();
	}

	protected void handleRequestWithNoDocumentResource() {
		throw new BadRequestException( "The request doesn't contain a document resource." );
	}

	protected abstract void validateDocumentResourceView( E documentResourceView );

	protected void validateRequestDocumentResourcesNumber( int number ) {
		if ( number == 0 ) throw new BadRequestException( "The request doesn't contain " );
	}

	protected void seekForOrphanFragments( AbstractModel requestModel, RDFResource requestDocumentResource ) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( ! URIUtil.hasFragment( subjectURI ) ) continue;
			URI documentURI = new URIImpl( URIUtil.getDocumentURI( subjectURI.stringValue() ) );
			if ( ! requestDocumentResource.getURI().equals( documentURI ) ) {
				throw new BadRequestException( "The request contains orphan fragments." );
			}
		}
	}

	protected void seekForOrphanFragments( AbstractModel requestModel, Set<RDFResource> requestDocumentResources ) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( ! URIUtil.hasFragment( subjectURI ) ) continue;
			URI documentURI = new URIImpl( URIUtil.getDocumentURI( subjectURI.stringValue() ) );
			RDFResource fragmentResource = new RDFResource( requestModel, documentURI );
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
