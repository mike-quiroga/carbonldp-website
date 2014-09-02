package com.base22.carbon.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.repository.services.DB2RepositoryService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;


public class DB2RepositoryServiceTest {
	
	static final String SCHEMA = "db2admin";
	static final String DATASET = "MAIN";
	
	static DB2RepositoryService db2RepositoryService;
	static String name;
	static Model model;
	static Dataset dataset;
		
	@BeforeClass
	public static void setUpBeforeClass() {
		db2RepositoryService = new DB2RepositoryService();
		db2RepositoryService.setDbUrl("jdbc:db2://localhost:50000/CTEST");
		db2RepositoryService.setDbUsername("db2admin");
		db2RepositoryService.setDbPassword("db2admin");
		
		name = "";
	}
	
	@Test
	public void testGetNamedModelUsingValidStoreName() throws RepositoryServiceException, SQLException{
		model = db2RepositoryService.getNamedModel(SCHEMA, DATASET);
		assertNotNull("Returned Model should not be null.", model);
	}
	
	@Test (expected = com.base22.carbon.repository.RepositoryServiceException.class)
	public void testGetNamedModelUsingInvalidStoreName() throws Exception {
		assertNotNull("Returned Model should not be null.", db2RepositoryService.getNamedModel(SCHEMA, "dummystore"));
	}
	
	@Test
	public void testgetDataset() throws Exception{
		assertNotNull("Returned dataset should not be null.", db2RepositoryService.getDataset(DATASET));
	}
	
	@Test
	public void testGetDatasets() throws Exception {
		List<String> datasets = db2RepositoryService.getDatasets();
		assertNotNull("Should not be null after getting the datasets", datasets);
	}
	
	@Test
	public void testRelease() throws Exception {
		db2RepositoryService.release();
		assertFalse("Should not have an open connection after release", db2RepositoryService.connectionIsOpen());
	}
	
	@AfterClass
	public static void cleanUpAfterClass() {
		db2RepositoryService = null;
	}
}
