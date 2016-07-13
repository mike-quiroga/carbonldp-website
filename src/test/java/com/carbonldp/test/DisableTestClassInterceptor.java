package com.carbonldp.test;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interceptor that makes sure methods are disabled if a class is marked with @Test( enabled = false ).
 * <br>
 * TestNG methods don't inherit the disabled status of their declaring class. This interceptor fixes that.
 *
 * @author MiguelAraCo
 * @see <a href="https://groups.google.com/forum/#!topic/testng-users/wMXc2HcDHMs">Google Group: How to disable all tests in a class</a>
 * @since 0.9.0-ALPHA
 */
public class DisableTestClassInterceptor implements IMethodInterceptor {
	@Override
	public List<IMethodInstance> intercept( List<IMethodInstance> list, ITestContext iTestContext ) {
		return list.stream()
		           .filter( this::declaringClassIsEnabled )
		           .collect( Collectors.toList() );

	}

	private boolean declaringClassIsEnabled( IMethodInstance methodInstance ) {
		Test testAnnotation = methodInstance.getMethod().getConstructorOrMethod().getDeclaringClass().getAnnotation( Test.class );

		return ( testAnnotation == null || testAnnotation.enabled() );
	}
}