package com.base22.carbon.services;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.base22.carbon.repository.DB2RepositoryService;
import com.base22.carbon.repository.RepositoryService;
import com.base22.carbon.repository.RepositoryServiceFactory;

public class RepositoryServiceFactoryTest {
	
	static RepositoryService repositoryService;
	static String string;
	static Map<String, RepositoryService> repositoryServices;
	
	static DB2RepositoryService db2;
	static String url;
	static String username;
	static String password;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		db2 = new DB2RepositoryService();
		repositoryServices = new HashMap<String, RepositoryService>();
		
		string = "DB2";
		url = "jdbc:db2://localhost:50000/CTEST";
		username = "db2admin";
		password = "db2admin";
		
		db2.setDbUrl(url);
		db2.setDbUsername(username);
		db2.setDbPassword(password);
	}

	@Test
	public void testCreate() {
		repositoryServices.put(string, db2);
		repositoryService = RepositoryServiceFactory.create(string, repositoryServices);
		assertEquals(db2, repositoryService);
	}	
	
	@AfterClass
	public static void cleanUpAfterClass() {
		db2 = null;
		repositoryServices = null;
		repositoryService = null;
	}
}
