package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.UUID;

public class SesameRDFRepresentationRepository extends AbstractSesameLDPRepository implements RDFRepresentationRepository {

	private FileRepository fileRepository;

	public SesameRDFRepresentationRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository, FileRepository fileRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );

		Assert.notNull( fileRepository );
		this.fileRepository = fileRepository;
	}

	@Override
	public void create( RDFRepresentation rdfRepresentation, File file, String mediaType ) {
		UUID fileUUID = fileRepository.save( file );

		rdfRepresentation.setSize( file.getTotalSpace() );
		rdfRepresentation.setMediaType( mediaType );
		rdfRepresentation.setUUID( fileUUID );

		documentRepository.addDocument( rdfRepresentation.getDocument() );
	}
}
