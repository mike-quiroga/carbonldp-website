package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

public class SesameRDFRepresentationRepository extends AbstractSesameLDPRepository implements RDFRepresentationRepository {

	public SesameRDFRepresentationRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public void create( URI containerURI, RDFRepresentation rdfRepresentation ) {

		addContainedResource( containerURI, rdfRepresentation.getURI() );
		addMemberRelation(containerURI,rdfRepresentation.getURI());
		documentRepository.addDocument( rdfRepresentation.getDocument() );
	}

	private void addMemberRelation( URI containerURI, URI resourceURI ) {
		
	}

	private void addContainedResource( URI containerURI, URI resourceURI ) {
		connectionTemplate.write( ( connection ) -> connection.add( containerURI, ContainerDescription.Property.CONTAINS.getURI(), resourceURI, containerURI ) );
	}
}
