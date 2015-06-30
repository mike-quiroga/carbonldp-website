package com.carbonldp.ldp.nonrdf;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.web.exceptions.NotImplementedException;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

public class SesameNonRDFResourceService extends AbstractSesameLDPService implements NonRDFResourceService {

	FileRepository fileRepository;

	public SesameNonRDFResourceService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, FileRepository fileRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.fileRepository = fileRepository;
	}

	@Override
	public File getResource( RDFRepresentation rdfRepresentation ) {
		String uuidString = rdfRepresentation.getUuid();
		UUID uuid = UUID.fromString( uuidString );
		InputStream inputStream = fileRepository.getFileAsInputStream( uuid );
		return inputStreamToFile( inputStream );
	}

	private File inputStreamToFile( InputStream inputStream ) {
		//TODO Implement
		throw new NotImplementedException();
	}
}
