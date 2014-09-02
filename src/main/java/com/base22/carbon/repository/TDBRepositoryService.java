package com.base22.carbon.repository;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

public class TDBRepositoryService implements RepositoryService {

	protected String datasetDirectory;

	static final Logger LOG = LoggerFactory.getLogger(TDBRepositoryService.class);
	static final Marker FATAL = MarkerFactory.getMarker("FATAL");

	protected Map<String, Dataset> datasetRegistry;
	protected Map<String, Model> namedModelRegistry;

	public void release() throws RepositoryServiceException {
		closeNamedModelRegistry();
		closeDatasetRegistry();

		// TODO: Remove this
		// Only for developing purposes. DO NOT USE IN PRODUCTION
		TDB.closedown();
	}

	private void closeDatasetRegistry() {
		if ( datasetRegistry == null ) {
			return;
		}
		if ( datasetRegistry.size() == 0 ) {
			return;
		}
		Iterator<Entry<String, Dataset>> datasetIterator = datasetRegistry.entrySet().iterator();
		while (datasetIterator.hasNext()) {
			Entry<String, Dataset> datasetEntry = datasetIterator.next();
			Dataset datasetToClose = datasetEntry.getValue();
			if ( datasetToClose.isInTransaction() ) {
				// TODO: Handle a dataset that is still in a transaction
				datasetToClose.commit();
			}
			try {
				TDB.sync(datasetToClose);
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx closeDatasetRegistry() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("-- closeDatasetRegistry() > Dataset '{}' couldn't be synched.", datasetEntry.getKey());
				}
			} finally {
				try {
					datasetToClose.close();
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx closeDatasetRegistry() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("-- closeDatasetRegistry() > Dataset '{}' couldn't be closed.", datasetEntry.getKey());
					}
				}
			}
		}
		datasetRegistry = null;
	}

	private void closeNamedModelRegistry() {
		if ( namedModelRegistry == null ) {
			return;
		}
		if ( namedModelRegistry.size() == 0 ) {
			return;
		}
		Iterator<Entry<String, Model>> namedModelIterator = namedModelRegistry.entrySet().iterator();
		while (namedModelIterator.hasNext()) {
			Entry<String, Model> namedModelEntry = namedModelIterator.next();
			Model namedModelToClose = namedModelEntry.getValue();

			try {
				TDB.sync(namedModelToClose);
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx closeNamedModelRegistry() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("-- closeNamedModelRegistry() > Named model '{}' couldn't be synched.", namedModelEntry.getKey());
				}
			} finally {
				try {
					// TODO: Is this needed?
					// namedModelToClose.close();
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx closeNamedModelRegistry() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("-- closeNamedModelRegistry() > Named model '{}' Couldn't be closed.", namedModelEntry.getKey());
					}
				}
			}
		}
		namedModelRegistry = null;

	}

	public Model getNamedModel(String name, String datasetName) throws RepositoryServiceException {
		Model namedModel = null;

		// Check if the dataset is already in the registry
		if ( namedModelRegistry != null ) {
			if ( namedModelRegistry.containsKey(name) ) {
				return namedModelRegistry.get(name);
			}
		} else {
			namedModelRegistry = new HashMap<String, Model>();
		}

		Dataset dataset = null;

		try {
			dataset = this.getDataset(datasetName);
		} catch (RepositoryServiceException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getNamedModel() > The dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw e;
		}

		DatasetTransactionUtil.beginDatasetTransaction(dataset, datasetName, ReadWrite.READ);

		try {
			namedModel = dataset.getNamedModel(name);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getNamedModel() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getNamedModel() > The named model: '{}', couldn't be retrieved.", name);
			}
		} finally {
			DatasetTransactionUtil.closeDatasetTransaction(dataset, datasetName, false);
		}

		namedModelRegistry.put(name, namedModel);

		return namedModel;
	}

	public void createDataset(String datasetName) throws RepositoryServiceException {
		Dataset dataset = null;

		String directory = datasetDirectory + datasetName;

		// Check if the dataset is already in the registry
		if ( datasetRegistry != null ) {
			if ( datasetRegistry.containsKey(datasetName) ) {
				// It is
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< createDataset() > The dataset '{}', already exists.", datasetName);
				}
				throw new RepositoryServiceException("The dataset already exists.");
			}
		} else {
			datasetRegistry = new HashMap<String, Dataset>();
		}
		// Check if the dataset already exists
		if ( datasetExists(datasetName) ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createDataset() > The dataset already exists");
			}
			throw new RepositoryServiceException("The dataset already exists.");
		}

		try {
			dataset = TDBFactory.createDataset(directory);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx createDataset() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createDataset() > The dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw new RepositoryServiceException("The dataset couldn't be retrieved.");
		}

		datasetRegistry.put(datasetName, dataset);
	}

	public Dataset getDataset(String datasetName) throws RepositoryServiceException {
		Dataset dataset = null;

		String directory = datasetDirectory + datasetName;

		// Check if the dataset is already in the registry
		if ( datasetRegistry != null ) {
			if ( datasetRegistry.containsKey(datasetName) ) {
				return datasetRegistry.get(datasetName);
			}
		} else {
			datasetRegistry = new HashMap<String, Dataset>();
		}

		// Check if the dataset exists
		if ( ! datasetExists(datasetName) ) {
			// It doesn't
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getDataset() > The dataset '{}', doesn't exist.", datasetName);
			}
			throw new RepositoryServiceException("The dataset doesn't exist.");
		}

		try {
			dataset = TDBFactory.createDataset(directory);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getDataset() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getDataset() > The dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw new RepositoryServiceException("The dataset couldn't be retrieved.");
		}

		datasetRegistry.put(datasetName, dataset);
		return dataset;
	}

	public List<String> getDatasets() throws RepositoryServiceException {
		List<String> datasets = null;

		// Get all the directories inside of the datasetDirectory
		File file = new File(datasetDirectory);
		String[] directories = file.list(new FilenameFilter() {

			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}

		});

		datasets = Arrays.asList(directories);

		return datasets;
	}

	public boolean datasetExists(String datasetName) throws RepositoryServiceException {
		boolean exists = false;

		try {
			File file = new File(datasetDirectory + datasetName);
			exists = file.exists() && file.isDirectory();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx datasetExists() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< datasetExists() > The directory for the dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw new RepositoryServiceException("The directory for the dataset couldn't be retrieved.");
		}

		return exists;
	}

	public void setDatasetDirectory(String datasetDirectory) {
		this.datasetDirectory = datasetDirectory;
	}

}
