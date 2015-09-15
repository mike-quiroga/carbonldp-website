package com.carbonldp.ldp;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.util.Assert;

public abstract class AbstractSesameLDPService<E extends BasicContainer> extends AbstractSesameService {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;
	protected final ACLRepository aclRepository;

	public AbstractSesameLDPService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper );
		Assert.notNull( sourceRepository );
		Assert.notNull( containerRepository );
		Assert.notNull( aclRepository );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
		this.aclRepository = aclRepository;
	}

}
