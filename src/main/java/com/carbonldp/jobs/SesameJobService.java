package com.carbonldp.jobs;

import com.carbonldp.authentication.ImportLDAPAgentsJob;
import com.carbonldp.authentication.ImportLDAPAgentsJobFactory;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResourceRepository;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
public class SesameJobService extends AbstractSesameLDPService implements JobService {
	private ContainerService containerService;
	private RDFSourceService sourceService;
	private ExecutionService executionService;
	private RDFResourceRepository resourceRepository;

	@Override
	public void create( IRI targetIRI, Job job ) {
		validate( job );
		containerService.createChild( targetIRI, job );
	}

	@Override
	public void createExecution( IRI jobIRI, Execution execution ) {
		containerService.createChild( jobIRI, execution );
		IRI executionQueueLocation = resourceRepository.getIRI( jobIRI, JobDescription.Property.EXECUTION_QUEUE_LOCATION );
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
			case IMPORT_LDAP_AGENTS_JOB:
				infractions = ImportLDAPAgentsJobFactory.getInstance().validate( job );
				checkPermissionsOverTheLDAP( job );
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

	private void checkPermissionsOverTheLDAP( Job job ) {
		ImportLDAPAgentsJob importLDAPAgentsJob = new ImportLDAPAgentsJob( job );
		IRI ldapIRI = importLDAPAgentsJob.getLDAPServerIRI();
		sourceService.get( ldapIRI );
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
	public void setResourceRepository( RDFResourceRepository resourceRepository ) {
		this.resourceRepository = resourceRepository;
	}
}
