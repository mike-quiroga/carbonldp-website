package com.carbonldp.apps.context;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.carbonldp.AbstractAspect;
import com.carbonldp.apps.Application;

@Aspect
public class ApplicationContextExchanger extends AbstractAspect {

	@Pointcut("inCarbonLDPPackage() && @annotation(com.carbonldp.apps.context.RunInPlatformContext)")
	private void runInPlatformContext() {
	}

	@Before("runInPlatformContext()")
	public void exchangeForPlatformContext() {
		ApplicationContext originalContext = ApplicationContextHolder.getContext();
		ApplicationContext platformContext = new TemporaryApplicationContext(originalContext);

		ApplicationContextHolder.setContext(platformContext);
	}

	@After("runInPlatformContext()")
	public void restoreFromPlatformContext() {
		restoreContext();
	}

	@Pointcut("inCarbonLDPPackage() && args(application,..) && @annotation(com.carbonldp.apps.context.RunInApplicationContext)")
	private void runInApplicationContext(Application application) {
	}

	@Before("runInApplicationContext(application)")
	public void exchangeForApplicationContext(Application application) {
		ApplicationContext originalContext = ApplicationContextHolder.getContext();
		ApplicationContext applicationContext = new TemporaryApplicationContext(originalContext);
		applicationContext.setApplication(application);

		ApplicationContextHolder.setContext(applicationContext);
	}

	@After("runInApplicationContext(application)")
	public void restoreFromApplicationContext(Application application) {
		restoreContext();
	}

	private void restoreContext() {
		ApplicationContext currentContext = ApplicationContextHolder.getContext();
		if ( ! (currentContext instanceof TemporaryApplicationContext) ) {
			// TODO: Throw exception. The authentication has changed during the method call
		}

		ApplicationContext originalContext = ((TemporaryApplicationContext) currentContext).getOriginalContext();
		ApplicationContextHolder.setContext(originalContext);
	}
}
