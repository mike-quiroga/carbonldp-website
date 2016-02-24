package com.carbonldp.jobs;

import com.carbonldp.apps.App;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author arima
 * @since _version_
 */
@Transactional
public class JobsExecutor {
	@Async
	public void runBackup( App app ) {
		//TODO: implement
	}
}
