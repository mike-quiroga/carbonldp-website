package com.carbonldp.jobs;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import com.carbonldp.jobs.JobDescription.JobStatus;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameJobService extends AbstractSesameLDPService implements JobService {
	private ContainerService containerService;
	private RDFSourceService sourceService;
	private JobRepository jobRepository;

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

	@Override
	public Job get( URI jobURI ) {
		return new Job( sourceService.get( jobURI ) );
	}

	@Override
	public void changeJobStatus( URI jobURI, JobStatus jobStatus ) {
		jobRepository.changeJobStatus( jobURI, jobStatus );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}

	@Autowired
	public void setJobRepository( JobRepository jobRepository ) {
		this.jobRepository = jobRepository;
	}
}
