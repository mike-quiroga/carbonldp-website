package com.carbonldp.log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ProviderCallsLogger extends AbstractComponentCallsLogger {
	@Override
	@Pointcut( "execution(* com.carbonldp..*Provider.*(..))" )
	protected void targetsComponent() {}
}
