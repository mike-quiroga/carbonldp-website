package com.carbonldp;

import org.aspectj.lang.annotation.Pointcut;

public abstract class AbstractAspect {
	@Pointcut( "execution(* com.carbonldp..*.*(..))" )
	protected void inCarbonLDPPackage() {
	}
}
