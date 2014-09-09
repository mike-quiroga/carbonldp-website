package com.base22.carbon.repository.services;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.base22.carbon.CarbonException;
import com.base22.carbon.jdbc.TransactionException;
import com.base22.carbon.repository.RepositoryServiceException;
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
				datasetToClose.end();
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

	private Dataset getDataset(String datasetName) throws RepositoryServiceException {
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

	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate(String datasetName) throws CarbonException {
		Dataset dataset = this.getDataset(datasetName);
		return new TDBReadTransactionTemplate<T>(datasetName, dataset);
	}

	public WriteTransactionTemplate getWriteTransactionTemplate(String datasetName) throws CarbonException {
		Dataset dataset = this.getDataset(datasetName);
		return new TDBWriteTransactionTemplate(datasetName, dataset);
	}

	private abstract class TDBTransactionTemplate {
		protected final String datasetName;
		protected final Dataset dataset;

		TDBTransactionTemplate(String datasetName, Dataset dataset) throws TransactionException {
			if ( ! dataset.supportsTransactions() ) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< constructor() > The dataset provided: '{}', doesn't support transactions.", datasetName);
				}
				throw new TransactionException("The dataset doesn't support transactions.");
			}

			this.datasetName = datasetName;
			this.dataset = dataset;
		}

		protected void beginDatasetTransaction(ReadWrite type) throws TransactionException {
			try {
				dataset.begin(type);
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx beginDatasetTransaction() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< beginDatasetTransaction() > A transaction couldn't be opened in the dataset '{}'.", datasetName);
				}
				throw new TransactionException("A transaction couldn't be opened in the dataset.");
			}
		}

		protected void endDatasetTransaction() throws TransactionException {
			try {
				dataset.end();
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx closeDatasetTransaction() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< closeDatasetTransaction() > A transaction couldn't be closed in the dataset '{}'.", datasetName);
				}
				throw new TransactionException("A transaction couldn't be finished.");
			}
		}
	}

	private class TDBWriteTransactionTemplate extends TDBTransactionTemplate implements WriteTransactionTemplate {

		TDBWriteTransactionTemplate(String datasetName, Dataset dataset) throws TransactionException {
			super(datasetName, dataset);
		}

		@Override
		public void execute(WriteTransactionCallback callback) throws CarbonException {
			beginDatasetTransaction(ReadWrite.WRITE);
			try {
				callback.executeInTransaction(dataset);
				commitDatasetTransaction();
			} catch (CarbonException e) {
				throw e;
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx execute() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< execute() > An error ocurred executing the callback.");
				}
				throw new TransactionException("An unexpected exception ocurred while executing the callback.");
			} finally {
				endDatasetTransaction();
			}
		}

		private void commitDatasetTransaction() throws TransactionException {
			try {
				dataset.commit();
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx commitDatasetTransaction() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< commitDatasetTransaction() > The dataset '{}', couldn't be commited.", datasetName);
				}
				throw new TransactionException("The dataset couldn't be commited.");
			}
		}
	}

	private class TDBReadTransactionTemplate<T> extends TDBTransactionTemplate implements ReadTransactionTemplate<T> {

		TDBReadTransactionTemplate(String datasetName, Dataset dataset) throws TransactionException {
			super(datasetName, dataset);
		}

		@Override
		public T execute(ReadTransactionCallback<T> callback) throws CarbonException {
			T result = null;

			beginDatasetTransaction(ReadWrite.READ);
			try {
				result = callback.executeInTransaction(dataset);
			} catch (CarbonException e) {
				throw e;
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx execute() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< execute() > An error ocurred executing the callback.");
				}
				throw new TransactionException("An unexpected exception ocurred while executing the callback.");
			} finally {
				endDatasetTransaction();
			}

			return result;
		}

	}
}
