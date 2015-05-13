package com.carbonldp.log;

import com.carbonldp.log.AbstractComponentCallsLogger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ControllerCallsLogger extends AbstractComponentCallsLogger {

	@Override
	@Pointcut( "execution(* com.carbonldp..*Controller.*(..))" )
	protected void targetsComponent() {}
}
