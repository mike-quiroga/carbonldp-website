package com.carbonldp.agents;

import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;

@Transactional
public class SesameAgentRepository extends AbstractSesameRepository implements AgentRepository {

	private RDFResourceRepository resourceRepository;
	private RDFDocumentRepository documentRepository;

	public SesameAgentRepository(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository) {
		super(connectionFactory);

		this.resourceRepository = resourceRepository;
		this.documentRepository = documentRepository;
	}

}
