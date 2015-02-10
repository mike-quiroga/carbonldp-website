package com.carbonldp.ldp.services;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;

public abstract class AbstractSesameLDPService extends AbstractSesameService {

	protected RDFResourceRepository resourceRepository;
	protected RDFDocumentRepository documentRepository;

	public AbstractSesameLDPService(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
			RDFDocumentRepository documentRepository) {
		super(connectionFactory);
		this.resourceRepository = resourceRepository;
		this.documentRepository = documentRepository;
	}

}
