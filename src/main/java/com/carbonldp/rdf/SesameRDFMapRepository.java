package com.carbonldp.rdf;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameRDFMapRepository extends AbstractSesameLDPRepository implements RDFMapRepository {
	public SesameRDFMapRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	RDFSourceRepository sourceRepository;

	public Set<RDFBlankNode> getEntries( IRI mapIRI ) {
		RDFSource sourceMap = sourceRepository.get( mapIRI );
		Set<BNode> entriesBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		Set<RDFBlankNode> entries = entriesBNodes.stream().map( entryBNode -> new RDFBlankNode( sourceMap.getBaseModel(), entryBNode ) ).collect( Collectors.toSet() );

		return entries;
	}

	public RDFBlankNode getEntry( IRI mapIRI, Value key ) {
		Set<BNode> entriesBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		RDFSource sourceMap = sourceRepository.get( mapIRI );
		RDFBlankNode entry;
		for ( BNode entryBNode : entriesBNodes ) {
			entry = new RDFBlankNode( sourceMap.getBaseModel(), entryBNode );
			if ( entry.getProperty( RDFMapDescription.EntryProperty.KEY ).equals( key ) )
				return entry;
		}
		return null;
	}

	public Set<Value> getKeys( IRI mapIRI ) {
		Set<BNode> entriesBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		Set<Value> keys = new HashSet<>();
		keys.addAll( entriesBNodes.stream()
		                          .map( entryBNode -> blankNodeRepository.getProperty( entryBNode, RDFMapDescription.EntryProperty.KEY, mapIRI ) )
		                          .collect( Collectors.toList() ) );
		return keys;
	}

	public boolean hasKey( IRI mapIRI, Value key ) {
		Set<BNode> entriesBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		for ( BNode entryBNode : entriesBNodes ) {
			Value persistedKey = blankNodeRepository.getProperty( entryBNode, RDFMapDescription.EntryProperty.KEY, mapIRI );
			if ( persistedKey.equals( key ) ) return true;
		}
		return false;
	}

	public Value getValue( IRI mapIRI, Value key ) {
		Set<BNode> entriesBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		for ( BNode entryBNode : entriesBNodes ) {
			Value persistedKey = blankNodeRepository.getProperty( entryBNode, RDFMapDescription.EntryProperty.KEY, mapIRI );
			if ( ! persistedKey.equals( key ) ) continue;
			return blankNodeRepository.getProperty( entryBNode, RDFMapDescription.EntryProperty.VALUE, mapIRI );
		}
		return null;
	}

	public Set<Value> getValues( IRI mapIRI, Value key ) {
		Set<BNode> entriesBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		for ( BNode entryBNode : entriesBNodes ) {
			Value persistedKey = blankNodeRepository.getProperty( entryBNode, RDFMapDescription.EntryProperty.KEY, mapIRI );
			if ( ! persistedKey.equals( key ) ) continue;
			return blankNodeRepository.getProperties( entryBNode, RDFMapDescription.EntryProperty.VALUE, mapIRI );
		}
		return null;
	}

	public void add( IRI mapIRI, Value key, Value... values ) {
		BNode entryBNode;
		if ( hasKey( mapIRI, key ) ) entryBNode = getEntry( mapIRI, key ).getSubject();
		else {
			entryBNode = SimpleValueFactory.getInstance().createBNode();
			resourceRepository.add( mapIRI, RDFMapDescription.Property.ENTRY.getIRI(), entryBNode );
			blankNodeRepository.add( entryBNode, RDFMapDescription.EntryProperty.KEY.getIRI(), key, mapIRI );
		}
		for ( Value value : values ) {
			blankNodeRepository.add( entryBNode, RDFMapDescription.EntryProperty.VALUE.getIRI(), value, mapIRI );
		}
	}

	public void remove( IRI mapIRI, Value key, Value... values ) {
		if ( ! hasKey( mapIRI, key ) ) return;
		BNode entryBNode = getEntry( mapIRI, key ).getSubject();

		for ( Value value : values ) {
			blankNodeRepository.remove( entryBNode, RDFMapDescription.EntryProperty.VALUE.getIRI(), value, mapIRI );
		}
		if ( ! blankNodeRepository.hasProperty( entryBNode, RDFMapDescription.EntryProperty.VALUE, mapIRI ) ) {
			blankNodeRepository.remove( entryBNode, RDFMapDescription.EntryProperty.KEY, mapIRI );
			resourceRepository.remove( mapIRI, RDFMapDescription.Property.ENTRY );
		}
	}

	public void remove( IRI mapIRI, Value key ) {
		if ( ! hasKey( mapIRI, key ) ) return;
		BNode entryBNode = getEntry( mapIRI, key ).getSubject();
		blankNodeRepository.remove( entryBNode, RDFMapDescription.EntryProperty.VALUE, mapIRI );
		blankNodeRepository.remove( entryBNode, RDFMapDescription.EntryProperty.KEY, mapIRI );
		resourceRepository.remove( mapIRI, RDFMapDescription.Property.ENTRY.getIRI(), entryBNode );
	}

	public void clean( IRI mapIRI ) {
		Set<BNode> entryBNodes = resourceRepository.getBNodes( mapIRI, RDFMapDescription.Property.ENTRY );
		for ( BNode entry : entryBNodes ) {
			if ( ! blankNodeRepository.hasProperty( entry, RDFMapDescription.EntryProperty.KEY, mapIRI ) ) {
				blankNodeRepository.remove( entry, RDFMapDescription.EntryProperty.VALUE, mapIRI );
				resourceRepository.remove( mapIRI, RDFMapDescription.Property.ENTRY.getIRI(), entry );
			}
		}
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
