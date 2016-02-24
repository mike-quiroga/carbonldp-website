package com.carbonldp.jobs;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameJobService extends AbstractSesameLDPService implements JobService {
	private ContainerService containerService;
	private RDFSourceService sourceService;

	public void create( URI targetURI, Job job ) {
		validate( job );
		URI jobURI = job.getURI();
		Trigger trigger = TriggerFactory.getInstance().create( job );
		containerService.createChild( targetURI, job );
		containerService.createChild( jobURI, trigger );
	}

	private void validate( Job job ) {
		List<Infraction> infractions = BackupJobFactory.getInstance().validate( job );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	public Job get( URI jobURI ) {
		return new Job( sourceService.get( jobURI ) );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}
}
