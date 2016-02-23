package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import com.carbonldp.jobs.TriggerDescription.Type;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameJobTriggerRepository extends AbstractSesameLDPRepository implements TypedTriggerRepository {
	public SesameJobTriggerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public boolean supports( Type triggerType ) {
		return triggerType == Type.JOB_TRIGGER;
	}

	@Override
	public void executeTrigger( URI triggerURI ) {
		// TODO: implement
		throw new NotImplementedException( "not implemented" );
	}
}
