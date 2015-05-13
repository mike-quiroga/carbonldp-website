package com.carbonldp.ldp.sources;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.RDFResourceUtil;
import com.carbonldp.utils.URIUtil;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	public SesameRDFSourceService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
	}

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

		// TODO: Move controller validation here
		DateTime creationTime = DateTime.now();

		accessPoint.setTimestamps( creationTime );
		sourceRepository.createAccessPoint( parentURI, accessPoint );
		sourceRepository.touch( parentURI, creationTime );

		return creationTime;
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
		// TODO: Validate the resource views don't target restricted properties

		sourceRepository.add( sourceURI, resourceViews );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void set( URI sourceURI, Collection<RDFResource> resourceViews ) {
		// TODO: Check that the RDFSource exists
		validateResourcesBelongToSource( sourceURI, resourceViews );
		// TODO: Validate the resource views don't target restricted properties

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
		// TODO: Validate the resource views don't target restricted properties

		Set<Statement> statementsToDelete = originalDocument.stream().filter( statement -> ! newDocument.contains( statement ) ).collect( Collectors.toSet() );
		Set<RDFResource> resourceViewsToDelete = RDFResourceUtil.getResourceViews( statementsToDelete );
		// TODO: Validate the resource views don't target restricted properties

		substract( originalSource.getURI(), resourceViewsToDelete );
		add( originalSource.getURI(), resourceViewsToAdd );

		sourceRepository.touch( source.getURI(), modifiedTime );

		return modifiedTime;
	}

	@Override
	public void substract( URI sourceURI, Collection<RDFResource> resourceViews ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

		validateResourcesBelongToSource( sourceURI, resourceViews );
		// TODO: Validate the resource views don't target restricted properties

		sourceRepository.substract( sourceURI, resourceViews );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void delete( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

		sourceRepository.delete( sourceURI );
		sourceRepository.deleteOccurrences( sourceURI, true );
	}

	private void validateResourcesBelongToSource( URI sourceURI, Collection<RDFResource> resourceViews ) {
		List<URI> resourceURIs = resourceViews.stream().map( RDFResource::getURI ).collect( Collectors.toList() );
		resourceURIs.add( sourceURI );
		URI[] uris = resourceURIs.toArray( new URI[resourceURIs.size()] );
		if ( ! URIUtil.belongsToSameDocument( uris ) ) throw new IllegalArgumentException( "The resourceViews don't belong to the source's document." );
	}
}
