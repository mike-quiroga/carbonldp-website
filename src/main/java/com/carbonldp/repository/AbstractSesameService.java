package com.carbonldp.repository;

import org.openrdf.spring.SesameConnectionFactory;

public abstract class AbstractSesameService extends AbstractSesameRepository {

	protected final RDFDocumentRepository documentRepository;

	public AbstractSesameService(SesameConnectionFactory connectionFactory, RDFDocumentRepository documentRepository) {
		super(connectionFactory);
		this.documentRepository = documentRepository;
	}

}
