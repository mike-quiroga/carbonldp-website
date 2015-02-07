package com.carbonldp.ldp.services;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.repository.RDFDocumentRepository;

public abstract class AbstractSesameLDPService extends AbstractSesameService {

	public AbstractSesameLDPService(SesameConnectionFactory connectionFactory, RDFDocumentRepository documentRepository) {
		super(connectionFactory, documentRepository);
	}

}
