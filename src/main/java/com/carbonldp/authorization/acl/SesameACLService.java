package com.carbonldp.authorization.acl;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;

public class SesameACLService extends AbstractSesameLDPService implements ACLService {
	public SesameACLService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( sourceRepository, containerRepository, aclRepository );
	}
}
