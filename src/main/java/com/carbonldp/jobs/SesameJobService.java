package com.carbonldp.jobs;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameJobService extends AbstractSesameLDPService implements JobService {
	private ContainerService containerService;
	private RDFSourceService sourceService;
	private ExecutionRepository executionRepository;
	private JobRepository jobRepository;

	public void create( URI targetURI, Job job ) {
		validate( job );
		containerService.createChild( targetURI, job );
	}

	public void createExecution( URI jobURI, Execution execution ) {
		containerService.createChild( jobURI, execution );
		URI executionQueueLocation = jobRepository.getExecutionQueueLocation( jobURI );
		executionRepository.enqueue( execution.getURI(), executionQueueLocation );
	}

	private void validate( Job job ) {
		List<Infraction> infractions = new ArrayList<>();
		JobDescription.Type jobType = JobFactory.getInstance().getJobType( job );
		switch ( jobType ) {
			case EXPORT_BACKUP_JOB:
				infractions = ExportBackupJobFactory.getInstance().validate( job );
				break;
			default:
				infractions.add( new Infraction( 0x2001, "rdf.type", "job type" ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public Job get( URI targetURI ) {
		return new Job( sourceService.get( targetURI ) );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setExecutionRepository( ExecutionRepository executionRepository ) {
		this.executionRepository = executionRepository;
	}

	@Autowired
	public void setJobRepository( JobRepository jobRepository ) {
		this.jobRepository = jobRepository;
	}
}
