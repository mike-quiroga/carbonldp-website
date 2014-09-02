package com.base22.carbon.services;

import com.base22.carbon.repository.DB2RepositoryService;
import com.base22.carbon.repository.RepositoryService;
import com.base22.carbon.repository.TDBRepositoryService;

public abstract class RepositoryServiceTestFactory {
	
	static TDBRepositoryService tbd;
	static DB2RepositoryService db2;
	static String datasetDirectory;
	static String datasetName;
	
	public enum SelectedRepository {DB2, TDB};
	public static RepositoryService create(SelectedRepository repository) throws Exception {
		switch(repository){
		case DB2:
			db2 = new DB2RepositoryService ();
			//further configuration
			db2.setDbUrl("jdbc:db2://localhost:50000/CTEST");
			db2.setDbUsername("db2admin");
			db2.setDbPassword("db2admin");
			return db2;
		case TDB:
			tbd = new TDBRepositoryService ();
			datasetDirectory = "C:\\TDB\\";
			
			tbd.setDatasetDirectory(datasetDirectory);
			
			//TODO: Probably this is not the best practice, but there's no other way of doing it... for now
			if (!tbd.datasetExists("main")) {
				tbd.createDataset("main");
			}
			
			return tbd;
		}
		
		return null;
	}

}
