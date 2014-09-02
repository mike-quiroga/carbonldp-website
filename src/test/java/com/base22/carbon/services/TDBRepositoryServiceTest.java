package com.base22.carbon.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.base22.carbon.repository.TDBRepositoryService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

public class TDBRepositoryServiceTest {

	static final String DATASET = "main";

	static TDBRepositoryService tDBRepositoryService;
	static String datasetDirectory;
	String documentName;
	static String datasetName;
	static Dataset dataset;

	@BeforeClass
	public static void SetUpBeforeClass() throws Exception {
		datasetDirectory = "C:\\TDB\\";

		tDBRepositoryService = new TDBRepositoryService();
		tDBRepositoryService.setDatasetDirectory(datasetDirectory);

		// TODO: Probably this is not the best practice, but there's no other way of doing it... for now
		if ( ! tDBRepositoryService.datasetExists("test") ) {
			tDBRepositoryService.createDataset("test");
		}
	}

	@Test
	public void testGetNamedModelUsingValidStoreName() throws Exception {
		documentName = "";
		Model model = tDBRepositoryService.getNamedModel(documentName, "test");
		assertNotNull("Returned Model should not be null.", model);
	}

	@Test(expected = com.base22.carbon.repository.RepositoryServiceException.class)
	public void testGetNamedModelUsingInvalidStoreName() throws Exception {
		documentName = "";
		assertNotNull("Returned Model should not be null.", tDBRepositoryService.getNamedModel(documentName, "dummystore"));
	}

	@Test
	public void testGetDataset() throws Exception {
		assertNotNull("Returned dataset should not be null.", tDBRepositoryService.getDataset("test"));
	}

	@Test
	public void testGetDatasets() throws Exception {
		List<String> datasets = tDBRepositoryService.getDatasets();
		assertNotNull("Should not be null after getting the datasets", datasets);
	}

	@Test
	public void testDatasetExists() throws Exception {
		assertTrue("A dataset with a valid name should exist", tDBRepositoryService.datasetExists("test"));
	}

	@Test
	public void testDatasetExistsInvalid() throws Exception {
		assertFalse("A dataset with an invalid name should not exist", tDBRepositoryService.datasetExists("dummydataset"));
	}

	/*
	 * @Test public void testRelease() throws Exception { tDBRepositoryService.release();
	 * assertTrue("DataRegistry should be null after release", tDBRepositoryService.isDatasetRegistryNull()); }
	 */
	@AfterClass
	public static void cleanUpAfterClass() {
		tDBRepositoryService = null;
	}
}
