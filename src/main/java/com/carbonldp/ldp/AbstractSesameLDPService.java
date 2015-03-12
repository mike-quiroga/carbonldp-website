package com.carbonldp.ldp;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameService;

public abstract class AbstractSesameLDPService extends AbstractSesameService {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;
	protected final ACLRepository aclRepository;

	public AbstractSesameLDPService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
		this.aclRepository = aclRepository;
	}
}
