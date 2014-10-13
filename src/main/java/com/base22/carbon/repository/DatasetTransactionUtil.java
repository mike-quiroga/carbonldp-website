package com.base22.carbon.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

public abstract class DatasetTransactionUtil {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetTransactionUtil.class);

	public static void beginDatasetTransaction(Dataset dataset, String datasetName, ReadWrite transactionType) throws RepositoryServiceException {
		try {
			if ( dataset.supportsTransactions() ) {
				dataset.begin(transactionType);
			}
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx beginDatasetTransaction() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< beginDatasetTransaction() > A transaction couldn't be opened in the dataset '{}'.", datasetName);
			}
			throw new RepositoryServiceException("A transaction couldn't be opened in the dataset.");
		}
	}

	public static void closeDatasetTransaction(Dataset dataset, String datasetName, boolean commit) throws RepositoryServiceException {
		if ( dataset.supportsTransactions() ) {
			if ( commit ) {
				commitDatasetTransaction(dataset, datasetName);
			} else {
				try {
					dataset.end();
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx closeDatasetTransaction() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< closeDatasetTransaction() > A transaction couldn't be closed in the dataset '{}'.", datasetName);
					}
				}
			}
		}
	}

	private static void commitDatasetTransaction(Dataset dataset, String datasetName) throws RepositoryServiceException {
		try {
			dataset.commit();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx commitDatasetTransaction() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< commitDatasetTransaction() > The dataset '{}', couldn't be commited.", datasetName);
			}
		} finally {
			closeDatasetTransaction(dataset, datasetName, false);
		}
	}
}
