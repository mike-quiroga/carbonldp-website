package com.carbonldp.ldp;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import org.openrdf.spring.SesameConnectionFactory;

public abstract class AbstractSesameLDPRepository extends AbstractSesameRepository {

	protected RDFResourceRepository resourceRepository;
	protected RDFDocumentRepository documentRepository;

	public AbstractSesameLDPRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory );
		this.resourceRepository = resourceRepository;
		this.documentRepository = documentRepository;
	}

}
