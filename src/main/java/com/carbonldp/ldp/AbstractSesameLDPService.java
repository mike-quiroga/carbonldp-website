package com.carbonldp.ldp;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSesameLDPService extends AbstractSesameService {
	protected RDFSourceRepository sourceRepository;
	protected ContainerRepository containerRepository;
	protected ACLRepository aclRepository;

	@Autowired
	public void setRDFSourceRepository( RDFSourceRepository sourceRepository ) { this.sourceRepository = sourceRepository; }

	@Autowired
	public void setContainerRepository( ContainerRepository containerRepository ) { this.containerRepository = containerRepository; }

	@Autowired
	public void setACLRepository( ACLRepository aclRepository ) { this.aclRepository = aclRepository; }
}
