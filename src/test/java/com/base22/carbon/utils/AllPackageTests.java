package com.base22.carbon.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		HttpUtilTest.class,
		ModelUtilTest.class,
		RdfUtilTest.class,
		SparqlUtilTest.class
	})
	
public class AllPackageTests {
}
