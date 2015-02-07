package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;

@Transactional
public class SesameAppRepository extends AbstractSesameRepository implements AppRepository {

	private RDFResourceRepository resourceRepository;
	private RDFDocumentRepository documentRepository;

	public SesameAppRepository(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository) {
		super(connectionFactory);

		this.resourceRepository = resourceRepository;
		this.documentRepository = documentRepository;
	}

	public Application get(URI applicationURI) {
		// TODO
		return null;
	}
}
