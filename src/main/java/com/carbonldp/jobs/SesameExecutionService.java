package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameExecutionService extends AbstractSesameLDPService implements ExecutionService {
	ExecutionRepository executionRepository;
	ContainerService containerService;

	public void createChild( URI executionsContainerURI, Execution execution ) {
		containerService.createChild( executionsContainerURI, execution );
		URI appURI = executionRepository.getAppRelatedURI( executionsContainerURI );
		executionRepository.enqueue( execution.getURI(), appURI );
	}

	@Autowired
	public void setExecutionRepository( ExecutionRepository executionRepository ) { this.executionRepository = executionRepository; }

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }
}
