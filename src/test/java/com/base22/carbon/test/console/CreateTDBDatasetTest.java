package com.base22.carbon.test.console;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.base22.carbon.CarbonException;
import com.base22.carbon.repository.services.TDBRepositoryService;

public class CreateTDBDatasetTest {
	
	// Use Linux /opt... path and it will work on both Linux 
	// and Windows. On Windows, see C:\opt...
	public static final String TDB_DIRECTORY = "/opt/carbon/TDB";
	
	public static final String TDB_DATASET_NAME = "platform";
	//public static final String TDB_DATASET_NAME = "platform-test";
	
	public static void main(String[] args) {
		CreateTDBDatasetTest datasetTest = new CreateTDBDatasetTest();
		datasetTest.execute();
	}

	public void execute() {

		// Create the TDB directory if it does not already exist...
		File tdbDir = new File(TDB_DIRECTORY);
		if( ! tdbDir.exists() ) {
			try {
				FileUtils.forceMkdir(tdbDir);
			} catch (IOException e) {
				System.out.println("Could not create directory: " + TDB_DIRECTORY);
				e.printStackTrace();
			}
		}
		
		TDBRepositoryService repositoryService = new TDBRepositoryService();
		repositoryService.setDatasetDirectory(TDB_DIRECTORY);
		
		try {
			repositoryService.createDataset(TDB_DATASET_NAME);
		} catch (CarbonException exception) {
			System.out.println("Couldn't create the dataset.");
			return;
		}
		System.out.println("The dataset was created.");
	}
	
}
