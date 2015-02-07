package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.commons.models.RDFDocument;
import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.repository.RDFDocumentRepository;

public class SesameAppService extends AbstractSesameService implements AppService {
	public SesameAppService(SesameConnectionFactory connectionFactory, RDFDocumentRepository documentRepository) {
		super(connectionFactory, documentRepository);
	}

	public Application get(URI appURI) {
		RDFDocument document = documentRepository.getDocument(appURI);

		return null;
	}
}
