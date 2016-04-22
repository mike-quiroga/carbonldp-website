package com.carbonldp.jobs;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Configuration
@EnableScheduling
@EnableAsync
public class JobConfig implements AsyncConfigurer {

	@Bean
	public JobManager jobManager() {
		return new JobManager();
	}

	@Bean
	public JobsExecutor jobsExecutor() {
		List<TypedJobExecutor> typedJobs = new ArrayList<>();
		typedJobs.add( exportBackupJobExecutor() );
		return new JobsExecutor( typedJobs );
	}

	@Bean
	public TypedJobExecutor exportBackupJobExecutor() {
		return new ExportBackupJobExecutor();
	}

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize( 2 );
		executor.setMaxPoolSize( 5 );
		executor.setQueueCapacity( 100 );
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncExceptionHandler();
	}

	class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

		@Override
		public void handleUncaughtException( Throwable throwable, Method method, Object... obj ) {
			throw new RuntimeException( "There was a problem running the job", throwable );
		}

	}
}
