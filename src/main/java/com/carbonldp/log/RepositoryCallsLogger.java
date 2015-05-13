package com.carbonldp.log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RepositoryCallsLogger extends AbstractComponentCallsLogger {
	@Override
	@Pointcut( "execution(* com.carbonldp..*Repository.*(..))" )
	protected void targetsComponent() {}
}
