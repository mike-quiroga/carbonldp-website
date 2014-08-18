package com.base22.carbon.services;

import java.util.List;

import com.base22.carbon.exceptions.RepositoryServiceException;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

public interface RepositoryService {
	/**
	 * Closes anything that has been opened for operation and provides a clean exit away from the repositoryService.
	 * Developers should normally call this just before returning the response.
	 * 
	 * @throws RepositoryServiceException
	 */
	public void release() throws RepositoryServiceException;

	public Model getNamedModel(String name, String dataset) throws RepositoryServiceException;

	public void createDataset(String datasetName) throws RepositoryServiceException;

	public Dataset getDataset(String dataset) throws RepositoryServiceException;

	public List<String> getDatasets() throws RepositoryServiceException;

	public boolean datasetExists(String dataset) throws RepositoryServiceException;
}
