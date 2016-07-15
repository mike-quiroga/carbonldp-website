package com.carbonldp.agents.validators;

import com.carbonldp.agents.AgentValidator;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SesameAgentValidatorRepository extends AbstractSesameLDPRepository implements AgentValidatorRepository {
	public SesameAgentValidatorRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	public void create( AgentValidator agentValidator ) {
		documentRepository.addDocument( agentValidator.getDocument() );
	}
}
