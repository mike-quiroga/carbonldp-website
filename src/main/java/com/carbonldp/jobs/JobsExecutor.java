package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerService;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Transactional
public class JobsExecutor {
	private AppRepository appRepository;
	private JobService jobService;

	@Async
	public void runJob( Job job ) {
		JobDescription.Type type = BackupJobFactory.getInstance().getJobType( job );
		switch ( type ) {
			case BACKUP:
				runBackupJob( job );
				break;
			default:
				// TODO: where can we put this change so it won't be rolled back?
				job.setJobStatus( BackupJobDescription.JobStatus.ERROR );
				throw new RuntimeException( "Invalid job type" );
		}
	}

	public void runBackupJob( Job job ) {
		jobService.changeJobStatus(job.getURI(), BackupJobDescription.JobStatus.RUNNING );
		URI appURI = job.getAppRelated();
		App app = appRepository.get( appURI );
		String appRepositoryID = app.getRepositoryID();
		String appNonRDFSourceDirectory = Vars.getInstance().getAppsRepositoryDirectory() + appRepositoryID;

	}

	public void createBackupContainer( Job job ) {

	}

	@Autowired
	public void setAppRepository( AppRepository appRepository ) {this.appRepository = appRepository; }

	@Autowired
	public void setJobService( JobService jobService ) {
		this.jobService = jobService;
	}
}
