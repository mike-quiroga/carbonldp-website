package com.carbonldp.jobs;

import com.carbonldp.apps.App;
import com.carbonldp.ldp.containers.ContainerDescription;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Transactional
public class JobsExecutor {
	@Async
	public void runJob( Job job ) {
		JobDescription.Type type = BackupJobFactory.getInstance().getJobType( job );
		switch ( type ) {
			case BACKUP:
				job.setJobStatus( BackupJobDescription.JobStatus.RUNNING );

		}
	}
}
