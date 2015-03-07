package com.carbonldp.authorization.acl;

import com.carbonldp.ldp.services.AbstractSesameLDPService;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import org.openrdf.spring.SesameConnectionFactory;

public class SesameACLRepository extends AbstractSesameLDPService implements ACLRepository {

	public SesameACLRepository(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

}
