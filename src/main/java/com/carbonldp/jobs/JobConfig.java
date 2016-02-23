package com.carbonldp.jobs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Configuration
@EnableScheduling
public class JobConfig {

	@Bean
	public JobManager jobManager() {
		return new JobManager();
	}
}
