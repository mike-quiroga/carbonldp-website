package com.carbonldp.log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ServiceCallsLogger extends AbstractComponentCallsLogger {
	@Override
	@Pointcut( "execution(* com.carbonldp..*Service.*(..))" )
	protected void targetsComponent() {}
}
