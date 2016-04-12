package com.carbonldp.jobs;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import org.openrdf.model.IRI;
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
	private ExecutionService executionService;
	private JobRepository jobRepository;

	@Override
	public void create( IRI targetIRI, Job job ) {
		validate( job );
		containerService.createChild( targetIRI, job );
	}

	@Override
	public void createExecution( IRI jobIRI, Execution execution ) {
		containerService.createChild( jobIRI, execution );
		IRI executionQueueLocation = jobRepository.getExecutionQueueLocation( jobIRI );
		executionService.enqueue( execution.getIRI(), executionQueueLocation );
	}

	private void validate( Job job ) {
		List<Infraction> infractions = new ArrayList<>();
		JobDescription.Type jobType = JobFactory.getInstance().getJobType( job );
		switch ( jobType ) {
			case EXPORT_BACKUP_JOB:
				infractions = ExportBackupJobFactory.getInstance().validate( job );
				break;
			case IMPORT_BACKUP_JOB:
				infractions = ImportBackupJobFactory.getInstance().validate( job );
				checkPermissionsOverTheBackup( job );
				break;
			default:
				infractions.add( new Infraction( 0x2001, "rdf.type", "job type" ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void checkPermissionsOverTheBackup( Job job ) {
		ImportBackupJob importBackupJob = new ImportBackupJob( job );
		IRI backupIRI = importBackupJob.getBackup();
		sourceService.get( backupIRI );
	}

	@Override
	public Job get( IRI targetIRI ) {
		return new Job( sourceService.get( targetIRI ) );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setExecutionService( ExecutionService executionService ) {
		this.executionService = executionService;
	}

	@Autowired
	public void setJobRepository( JobRepository jobRepository ) {
		this.jobRepository = jobRepository;
	}
}
