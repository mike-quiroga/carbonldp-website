package com.carbonldp.ldp;

import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;

public abstract class AbstractSesameLDPService {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;

	public AbstractSesameLDPService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
	}
}
