package com.base22.carbon.test.console;

import com.base22.carbon.CarbonException;
import com.base22.carbon.repository.services.OracleSDBRepositoryService;

public class PrepareSDBDatasetTest {
	public static void main(String[] args) {
		PrepareSDBDatasetTest datasetTest = new PrepareSDBDatasetTest();
		datasetTest.execute();
	}

	public void execute() {
		OracleSDBRepositoryService repositoryService = new OracleSDBRepositoryService();
		repositoryService.setJdbcBaseURL("jdbc:oracle:thin:@localhost:1521:");
		repositoryService.setOracleUser("SYSTEM");
		repositoryService.setOraclePassword("oracleadmin");

		try {
			repositoryService.formatDatabase("XE");
		} catch (CarbonException exception) {
			System.out.println("Couldn't format the database.");
			return;
		}
		System.out.println("The database was formatted.");

	}
}
