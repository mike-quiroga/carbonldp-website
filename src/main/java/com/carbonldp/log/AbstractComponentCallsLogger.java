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
		if ( ! LOG.isTraceEnabled() ) return;

		Logger LOG = LoggerFactory.getLogger( joinPoint.getTarget().getClass() );

		Object[] args = joinPoint.getArgs();
		if ( args == null ) LOG.trace( ">> {}() -> {}", joinPoint.getSignature().getName(), args );
		else LOG.trace( ">> {}() -> {}", joinPoint.getSignature().getName(), this.convertArgsToString( args ) );
	}

	@AfterReturning(
		pointcut = "targetsComponent()",
		returning = "returnedValue"
	)
	public void controllerMethodReturned( JoinPoint joinPoint, Object returnedValue ) {
		if ( ! LOG.isTraceEnabled() ) return;
		Logger LOG = LoggerFactory.getLogger( joinPoint.getTarget().getClass() );
		if ( returnedValue == null ) LOG.trace( "<< {}() <- {}", joinPoint.getSignature().getName(), returnedValue );
		else LOG.trace( "<< {}() <- {}", joinPoint.getSignature().getName(), returnedValue.toString() );
	}

	@AfterThrowing( "targetsComponent()" )
	public void controllerMethodThrew( JoinPoint joinPoint ) {
		if ( LOG.isTraceEnabled() ) {
			Logger LOG = LoggerFactory.getLogger( joinPoint.getTarget().getClass() );
			LOG.trace( "xx {}", joinPoint.getSignature().getName() );
		}
	}

	private String convertArgsToString( Object[] args ) {
		if ( args.length == 0 ) return "[]";

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "[" );
		for ( int i = 0, length = args.length; i < length; i++ ) {
			Object arg = args[i];
			if ( arg == null ) stringBuilder.append( "null" );
			else stringBuilder.append( arg.toString() );

			if ( ( i + 1 ) < length ) stringBuilder.append( ", " );
		}
		stringBuilder.append( "]" );
		return stringBuilder.toString();
	}
}
