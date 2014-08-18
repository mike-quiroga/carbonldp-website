package com.base22.carbon.services;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// Since SparqlService is lower-level, the order of testing must come before RepositoryService...
		SparqlServiceTest.class,
		DB2RepositoryServiceTest.class,
		RepositoryServiceFactoryTest.class
	})
	
public class AllPackageTests {
}
