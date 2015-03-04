package com.carbonldp.authorization.acl;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.ldp.services.AbstractSesameLDPService;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;

public class SesameACLRepository extends AbstractSesameLDPService implements ACLRepository {

	public SesameACLRepository(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository) {
		super(connectionFactory, resourceRepository, documentRepository);
	}

}
