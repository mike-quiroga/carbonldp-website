package com.carbonldp.log;

import com.carbonldp.AbstractAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public abstract class AbstractComponentCallsLogger extends AbstractAspect {

	@Pointcut
	protected abstract void targetsComponent();

	@Before( "targetsComponent()" )
	public void controllerMethodCalled( JoinPoint joinPoint ) {
		if ( LOG.isTraceEnabled() ) {
			Logger LOG = LoggerFactory.getLogger( joinPoint.getTarget().getClass() );
			LOG.trace( ">> {}() -> {}", joinPoint.getSignature().getName(), joinPoint.getArgs() );
		}
	}

	@AfterReturning(
		pointcut = "targetsComponent()",
		returning = "returnedValue"
	)
	public void controllerMethodReturned( JoinPoint joinPoint, Object returnedValue ) {
		if ( LOG.isTraceEnabled() ) {
			Logger LOG = LoggerFactory.getLogger( joinPoint.getTarget().getClass() );
			LOG.trace( "<< {}() <- {}", joinPoint.getSignature().getName(), returnedValue );
		}
	}

	@AfterThrowing( "targetsComponent()" )
	public void controllerMethodThrew( JoinPoint joinPoint ) {
		if ( LOG.isTraceEnabled() ) {
			Logger LOG = LoggerFactory.getLogger( joinPoint.getTarget().getClass() );
			LOG.trace( "xx {}", joinPoint.getSignature().getName() );
		}
	}
}
