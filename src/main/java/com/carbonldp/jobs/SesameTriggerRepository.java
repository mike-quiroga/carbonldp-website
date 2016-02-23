package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.jobs.TriggerDescription.Type;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameTriggerRepository extends AbstractSesameLDPRepository implements TriggerRepository {
	List<TypedTriggerRepository> typedTriggerRepositories;

	public SesameTriggerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository, List<TypedTriggerRepository> typedTriggerRepositories ) {
		super( connectionFactory, resourceRepository, documentRepository );

		Assert.notNull( typedTriggerRepositories );
		Assert.notEmpty( typedTriggerRepositories );
		this.typedTriggerRepositories = typedTriggerRepositories;
	}

	@Override
	public void executeTrigger( URI triggerURI ) {
		Type triggerType = getTriggerType( triggerURI );
		if ( triggerType == null ) throw new IllegalStateException( "The resource isn't a trigger." );

		executeTrigger( triggerURI, triggerType );
	}

	@Override
	public void executeTrigger( URI triggerURI, Type triggerType ) {
		getTypedRepository( triggerType ).executeTrigger( triggerURI );
	}

	private Type getTriggerType( URI triggerURI ) {
		Set<URI> resourceTypes = resourceRepository.getTypes( triggerURI );
		return TriggerFactory.getInstance().getTriggerType( resourceTypes );
	}

	@Override
	public TypedTriggerRepository getTypedRepository( Type triggerType ) {
		for ( TypedTriggerRepository service : typedTriggerRepositories ) {
			if ( service.supports( triggerType ) ) return service;
		}
		throw new IllegalArgumentException( "The containerType provided isn't supported" );
	}
}
