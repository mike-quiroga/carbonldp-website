package com.carbonldp.jobs;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author arima
 * @since _version_
 */
@Transactional
public class JobsExecutor {

	@Async
	public void specialMethod() {
		try {
			Thread.sleep( 2000 );
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		System.out.println( "imprimo 2" );
	}

	@Async
	public void specialMethod2() {
		System.out.println( "imprimo 3" );
	}
}
