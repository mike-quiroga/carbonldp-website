package com.carbonldp.ldp.nonrdf;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;

import java.io.File;
import java.util.UUID;

public class SesameNonRDFSourceService extends AbstractSesameLDPService implements NonRDFSourceService {

	FileRepository fileRepository;

	public SesameNonRDFSourceService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, FileRepository fileRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.fileRepository = fileRepository;
	}

	@Override
	public File getResource( RDFRepresentation rdfRepresentation ) {
		String uuidString = rdfRepresentation.getUUID();
		UUID uuid = UUID.fromString( uuidString );

		return fileRepository.get( uuid );

	}

	@Override
	public boolean isRDFRepresentation( URI targetURI ) {
		RDFSource source = sourceRepository.get( targetURI );
		return source.getTypes().contains( RDFRepresentationDescription.Resource.NON_RDF_SOURCE.getURI() );
	}
}
