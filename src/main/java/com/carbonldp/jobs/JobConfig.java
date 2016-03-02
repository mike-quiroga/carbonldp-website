package com.carbonldp.jobs;

import org.openrdf.model.Literal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Configuration
@EnableScheduling
@EnableAsync
public class JobConfig {

	@Bean
	public JobManager jobManager() {
		return new JobManager();
	}

	@Bean
	public JobsExecutor jobsExecutor() {
		List<TypedJobExecutor> typedJobs = new ArrayList<>();
		typedJobs.add( backupJobExecutor() );
		return new JobsExecutor( typedJobs );
	}

	@Bean
	public TypedJobExecutor backupJobExecutor() {
		return new BackupJobExecutor();
	}


}
