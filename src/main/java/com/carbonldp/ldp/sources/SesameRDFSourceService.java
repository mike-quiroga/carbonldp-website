package com.carbonldp.ldp.sources;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.ldp.containers.AccessPointFactory;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.ContainerFactory;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFResourceUtil;
import com.carbonldp.utils.URIUtil;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	private NonRDFSourceService nonRdfSourceService;

	@Override
	public boolean exists( URI sourceURI ) {
		return sourceRepository.exists( sourceURI );
	}

	@Override
	public RDFSource get( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.get( sourceURI );
	}

	@Override
	public DateTime getModified( URI sourceURI ) {

		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.getModified( sourceURI );
	}

	@Override
	public URI getDefaultInteractionModel( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.getDefaultInteractionModel( sourceURI );
	}

	@Override
	public DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint ) {
		if ( ! exists( parentURI ) ) throw new ResourceDoesntExistException();
		DateTime creationTime = DateTime.now();
		accessPoint.setTimestamps( creationTime );
		validate( accessPoint, parentURI );
		sourceRepository.createAccessPoint( parentURI, accessPoint );
		sourceRepository.touch( parentURI, creationTime );
		aclRepository.createACL( accessPoint.getURI() );
		return creationTime;
	}

	private void validate( AccessPoint accessPoint, URI parentURI ) {
		List<Infraction> infractions = AccessPointFactory.getInstance().validate( accessPoint, parentURI );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public void touch( URI sourceURI, DateTime now ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		sourceRepository.touch( sourceURI, now );
	}

	@Override
	public void add( URI sourceURI, Collection<RDFResource> resourceViews ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

		validateResourcesBelongToSource( sourceURI, resourceViews );
		containsImmutableProperties( resourceViews );

		sourceRepository.add( sourceURI, resourceViews );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void set( URI sourceURI, Collection<RDFResource> resourceViews ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		containsImmutableProperties( resourceViews );
		validateResourcesBelongToSource( sourceURI, resourceViews );

		sourceRepository.set( sourceURI, resourceViews );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public DateTime replace( RDFSource source ) {
		DateTime modifiedTime = DateTime.now();

		RDFSource originalSource = get( source.getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		RDFDocument newDocument = source.getDocument();

		Set<Statement> statementsToAdd = newDocument.stream().filter( statement -> ! originalDocument.contains( statement ) ).collect( Collectors.toSet() );
		Set<RDFResource> resourceViewsToAdd = RDFResourceUtil.getResourceViews( statementsToAdd );
		containsImmutableProperties( resourceViewsToAdd );

		Set<Statement> statementsToDelete = originalDocument.stream().filter( statement -> ! newDocument.contains( statement ) ).collect( Collectors.toSet() );
		Set<RDFResource> resourceViewsToDelete = RDFResourceUtil.getResourceViews( statementsToDelete );
		containsImmutableProperties( resourceViewsToDelete );

		substract( originalSource.getURI(), resourceViewsToDelete );
		add( originalSource.getURI(), resourceViewsToAdd );

		sourceRepository.touch( source.getURI(), modifiedTime );

		return modifiedTime;
	}

	private void containsImmutableProperties( Collection<RDFResource> resources ) {
		List<Infraction> infractions = new ArrayList<>();
		for ( RDFResource resource : resources ) {
			infractions.addAll( ContainerFactory.getInstance().validateImmutableProperties( resource ) );
			infractions.addAll( ContainerFactory.getInstance().validateSystemManagedProperties( resource ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public void substract( URI sourceURI, Collection<RDFResource> resourceViews ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

		validateResourcesBelongToSource( sourceURI, resourceViews );
		containsImmutableProperties( resourceViews );

		sourceRepository.subtract( sourceURI, resourceViews );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void delete( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		if ( nonRdfSourceService.isRDFRepresentation( sourceURI ) ) {
			RDFRepresentation rdfRepresentation = new RDFRepresentation( get( sourceURI ) );
			nonRdfSourceService.deleteResource( rdfRepresentation );
		}
		sourceRepository.delete( sourceURI );
		sourceRepository.deleteOccurrences( sourceURI, true );
	}

	private void validateSystemManagedProperties( Collection<RDFResource> resourceViews ) {
		for ( RDFResource resource : resourceViews ) {
			if ( ! BasicContainerFactory.getInstance().validateSystemManagedProperties( resource ).isEmpty() ) throw new IllegalArgumentException( "System properties can not be changed" );
		}
	}

	private void validateResourcesBelongToSource( URI sourceURI, Collection<RDFResource> resourceViews ) {
		List<URI> resourceURIs = resourceViews.stream().map( RDFResource::getURI ).collect( Collectors.toList() );
		resourceURIs.add( sourceURI );
		URI[] uris = resourceURIs.toArray( new URI[resourceURIs.size()] );
		if ( ! URIUtil.belongsToSameDocument( uris ) ) throw new IllegalArgumentException( "The resourceViews don't belong to the source's document." );
	}

	@Autowired
	public void setNonRDFSourceService( NonRDFSourceService nonRdfSourceService ) { this.nonRdfSourceService = nonRdfSourceService; }
}
