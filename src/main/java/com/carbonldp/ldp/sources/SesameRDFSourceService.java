package com.carbonldp.ldp.sources;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.ldp.containers.AccessPointFactory;
import com.carbonldp.ldp.containers.ContainerFactory;
import com.carbonldp.ldp.nonrdf.NonRDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.*;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	private NonRDFSourceRepository nonRDFSourceRepository;

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
	public Set<RDFSource> get( Set<URI> sourceURIs ) {
		for ( URI sourceURI : sourceURIs ) if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.get( sourceURIs );
	}

	@Override
	public String getETag( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.getETag( sourceURI );
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

		containsImmutableProperties( document );
		validateBNodesUniqueIdentifier( document );

		document = handleBlankNodesIdentifiers( document );

		documentRepository.add( sourceURI, document );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public void set( URI sourceURI, RDFDocument document ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		containsImmutableProperties( document );

		documentRepository.set( sourceURI, document );

		sourceRepository.touch( sourceURI );
	}

	@Override
	public DateTime replace( RDFSource source ) {
		DateTime modifiedTime = DateTime.now();

		RDFSource originalSource = get( source.getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		RDFDocument newDocument = mapBNodeSubjects( originalDocument, source.getDocument() );

		AbstractModel toAdd = newDocument.stream().filter( statement -> ! originalDocument.contains( statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToAdd = new RDFDocument( toAdd, source.getURI() );

		AbstractModel toDelete = originalDocument.stream().filter( statement -> ! newDocument.contains( statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToDelete = new RDFDocument( toDelete, source.getURI() );

		subtract( originalSource.getURI(), documentToDelete );
		add( originalSource.getURI(), documentToAdd );

		sourceRepository.touch( source.getURI(), modifiedTime );

		return modifiedTime;
	}

	@Override
	public void subtract( URI sourceURI, RDFDocument document ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		RDFSource originalSource = get( document.getDocumentResource().getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		document = mapBNodeSubjects( originalDocument, document );

		containsImmutableProperties( originalDocument, document );
		document = addIdentifierToRemoveIfBNodeIsEmpty( originalDocument, document );

		documentRepository.subtract( sourceURI, document );

		sourceRepository.touch( sourceURI );
	}

	private RDFDocument addIdentifierToRemoveIfBNodeIsEmpty( RDFDocument originalDocument, RDFDocument document ) {
		URI resourceURI = originalDocument.getDocumentResource().getURI();

		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode newBlankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, resourceURI );
			RDFBlankNode originalBlankNode = new RDFBlankNode( originalDocument.getBaseModel(), subjectBNode, resourceURI );
			if ( originalBlankNode.size() - newBlankNode.size() != 1 ) continue;
			newBlankNode.setIdentifier( originalBlankNode.getIdentifier() );
		}

		return document;
	}

	@Override
	public void delete( URI sourceURI ) {
		if ( ! exists( sourceURI ) ) throw new ResourceDoesntExistException();
		nonRDFSourceRepository.delete( sourceURI );
		sourceRepository.delete( sourceURI );
		sourceRepository.deleteOccurrences( sourceURI, true );
	}

	private void validateResourcesBelongToSource( URI sourceURI, Collection<RDFResource> resourceViews ) {
		List<URI> resourceURIs = resourceViews.stream().map( RDFResource::getURI ).collect( Collectors.toList() );
		resourceURIs.add( sourceURI );
		URI[] uris = resourceURIs.toArray( new URI[resourceURIs.size()] );
		if ( ! URIUtil.belongsToSameDocument( uris ) ) throw new IllegalArgumentException( "The resourceViews don't belong to the source's document." );
	}

	private RDFDocument handleBlankNodesIdentifiers( RDFDocument document ) {
		RDFSource originalSource = get( document.getDocumentResource().getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		document = mapBNodeSubjects( originalDocument, document );

		Set<Resource> originalSubjects = originalDocument.subjects();
		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			if ( originalSubjects.contains( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode blankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, originalSource.getURI() );
			if ( blankNode.getIdentifier() != null ) {
				if ( blankNode.size() == 1 ) document.removeAll( blankNode );
				continue;
			}
			RDFBlankNodeFactory.setIdentifier( blankNode );
		}

		return document;
	}

	private void containsImmutableProperties( RDFDocument document ) {
		List<Infraction> infractions = validateDocumentContainsImmutableProperties( document );

		infractions.addAll( RDFDocumentFactory.getInstance().validateBlankNodes( document ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void containsImmutableProperties( RDFDocument originalDocument, RDFDocument document ) {
		List<Infraction> infractions = validateDocumentContainsImmutableProperties( document );

		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode originalBlankBode = new RDFBlankNode( originalDocument.getBaseModel(), subjectBNode, originalDocument.getDocumentResource().getURI() );
			RDFBlankNode newBlankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, document.getDocumentResource().getURI() );
			if ( originalBlankBode.size() == newBlankNode.size() ) continue;
			infractions.addAll( RDFDocumentFactory.getInstance().validateBlankNodes( document ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private List<Infraction> validateDocumentContainsImmutableProperties( RDFDocument document ) {
		List<Infraction> infractions = new ArrayList<>();
		infractions.addAll( ContainerFactory.getInstance().validateImmutableProperties( document.getDocumentResource() ) );
		infractions.addAll( ContainerFactory.getInstance().validateSystemManagedProperties( document.getDocumentResource() ) );
		return infractions;
	}

	private RDFDocument mapBNodeSubjects( RDFDocument originalDocument, RDFDocument newDocument ) {
		URI documentUri = newDocument.getDocumentResource().getURI();

		Set<String> originalDocumentBlankNodesIdentifier = originalDocument.getBlankNodesIdentifier();
		Set<String> newDocumentBlankNodesIdentifiers = newDocument.getBlankNodesIdentifier();
		for ( String newDocumentBlankNodeIdentifier : newDocumentBlankNodesIdentifiers ) {
			if ( ! originalDocumentBlankNodesIdentifier.contains( newDocumentBlankNodeIdentifier ) ) continue;

			RDFBlankNode newBlankNode = newDocument.getBlankNode( newDocumentBlankNodeIdentifier );
			BNode originalBNode = originalDocument.getBlankNode( newDocumentBlankNodeIdentifier ).getSubject();

			if ( newBlankNode.getSubject().equals( originalBNode ) ) continue;

			Map<URI, Set<Value>> propertiesMap = newBlankNode.getPropertiesMap();
			Set<URI> properties = propertiesMap.keySet();

			for ( URI property : properties ) {
				Set<Value> objects = propertiesMap.get( property );
				for ( Value object : objects ) {
					newDocument.getBaseModel().add( originalBNode, property, object, documentUri );
				}
			}
			newDocument.subjects().remove( newBlankNode.getSubject() );

		}
		return newDocument;
	}

	private void validateBNodesUniqueIdentifier( RDFDocument document ) {
		Set<RDFBlankNode> blankNodes = document.getBlankNodes();
		List<Infraction> infractions = new ArrayList<>();
		URI documentURI = document.getDocumentResource().getDocumentURI();
		for ( RDFBlankNode blankNode : blankNodes ) {
			if ( blankNode.getIdentifier() == null ) continue;
			if ( blankNodeRepository.hasProperty( blankNode.getSubject(), RDFBlankNodeDescription.Property.BNODE_IDENTIFIER, documentURI ) )
				infractions.add( new Infraction( 0x2004, "property", RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getURI().stringValue() ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setNonRDFSourceService( NonRDFSourceRepository nonRDFSourceRepository ) { this.nonRDFSourceRepository = nonRDFSourceRepository; }
}
