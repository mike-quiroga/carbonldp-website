package com.carbonldp.apps.context;

import com.carbonldp.AbstractAspect;
import com.carbonldp.apps.App;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AppContextExchanger extends AbstractAspect {

	@Pointcut( "inCarbonLDPPackage() && @annotation(com.carbonldp.apps.context.RunInPlatformContext)" )
	private void runInPlatformContext() {
	}

	@Before( "runInPlatformContext()" )
	public void exchangeForPlatformContext() {
		AppContext originalContext = AppContextHolder.getContext();
		AppContext platformContext = new TemporaryAppContext( originalContext );

		AppContextHolder.setContext( platformContext );
	}

	@After( "runInPlatformContext()" )
	public void restoreFromPlatformContext() {
		restoreContext();
	}

	@Pointcut( "inCarbonLDPPackage() && args(application,..) && @annotation(com.carbonldp.apps.context.RunInAppContext)" )
	private void runInAppContext( App application ) {
	}

	@Before( "runInAppContext(application)" )
	public void exchangeForApplicationContext( App application ) {
		AppContext originalContext = AppContextHolder.getContext();
		AppContext applicationContext = new TemporaryAppContext( originalContext );
		applicationContext.setApplication( application );

		AppContextHolder.setContext( applicationContext );
	}

	@After( "runInAppContext(application)" )
	public void restoreFromApplicationContext( App application ) {
		restoreContext();
	}

	private void restoreContext() {
		AppContext currentContext = AppContextHolder.getContext();
		if ( ! ( currentContext instanceof TemporaryAppContext ) ) {
			// TODO: Throw exception. The authentication has changed during the method call
		}

		AppContext originalContext = ( (TemporaryAppContext) currentContext ).getOriginalContext();
		AppContextHolder.setContext( originalContext );
	}
}
