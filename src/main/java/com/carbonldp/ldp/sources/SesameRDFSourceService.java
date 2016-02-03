package com.carbonldp.ldp.sources;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.*;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
	public void add( URI sourceURI, RDFDocument document ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

		validateResourcesBelongToSource( sourceURI, document.getFragmentResources() );
		containsImmutableProperties( document );
		document = designBlankNodesIdentifiers( document );

		sourceRepository.add( sourceURI, document );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void set( URI sourceURI, RDFDocument document ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		containsImmutableProperties( document );
		validateResourcesBelongToSource( sourceURI, document.getFragmentResources() );

		sourceRepository.set( sourceURI, document );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public DateTime replace( RDFSource source ) {
		DateTime modifiedTime = DateTime.now();

		RDFSource originalSource = get( source.getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		RDFDocument newDocument = normalizeBNodes( originalDocument, source.getDocument() );

		AbstractModel toAdd = newDocument.stream().filter( statement -> ! originalDocument.contains( statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToAdd = new RDFDocument( toAdd, source.getURI() );
		containsImmutableProperties( documentToAdd );

		AbstractModel toDelete = originalDocument.stream().filter( statement -> ! newDocument.contains( statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToDelete = new RDFDocument( toDelete, source.getURI() );
		containsImmutableProperties( documentToDelete );

		subtract( originalSource.getURI(), documentToDelete );
		add( originalSource.getURI(), documentToAdd );

		sourceRepository.touch( source.getURI(), modifiedTime );

		return modifiedTime;
	}

	private void containsImmutableProperties( RDFDocument document ) {
		List<Infraction> infractions = new ArrayList<>();

		infractions.addAll( ContainerFactory.getInstance().validateImmutableProperties( document.getDocumentResource() ) );
		infractions.addAll( ContainerFactory.getInstance().validateSystemManagedProperties( document.getDocumentResource() ) );
		Set<RDFBlankNode> blankNodes = document.getBlankNodes();
		infractions.addAll( RDFDocumentFactory.getInstance().validateBlankNodes( document ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public void subtract( URI sourceURI, RDFDocument document ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

		validateResourcesBelongToSource( sourceURI, document.getFragmentResources() );
		containsImmutableProperties( document );

		sourceRepository.subtract( sourceURI, document );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void delete( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();

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

	private RDFDocument designBlankNodesIdentifiers( RDFDocument document ) {
		RDFSource originalSource = get( document.getDocumentResource().getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		document = normalizeBNodes( originalDocument, document );

		Set<Resource> originalSubjects = originalDocument.subjects();
		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			if ( originalSubjects.contains( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode blankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, originalSource.getURI() );
			RDFBlankNodeFactory.setIdentifier( blankNode );
		}

		return document;
	}

}
