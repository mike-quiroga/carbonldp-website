package com.base22.carbon.sparql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.base22.carbon.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.services.RepositoryService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;

public class SPARQLExecutor {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected Dataset getDataset(String datasetName, RepositoryService repositoryService) throws CarbonException {
		Dataset dataset = null;
		try {
			dataset = repositoryService.getDataset(datasetName);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
		return dataset;
	}

	protected void beginDatasetTransaction(String datasetName, Dataset dataset, ReadWrite transactionType) throws CarbonException {
		try {
			dataset.begin(transactionType);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx beginDatasetTransaction() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- beginDatasetTransaction() > The dataset: couldn't be started.", datasetName);
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}

	protected void endDatasetTransaction(String datasetName, Dataset dataset) throws CarbonException {
		try {
			dataset.end();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx endDatasetTransaction() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- endDatasetTransaction() > The transaction under the dataset: '{}', couldn't be finished.", datasetName);
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}

	protected void enterModelCriticalSection(Model domainModel, boolean read) throws CarbonException {
		try {
			domainModel.enterCriticalSection(read);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx enterModelCriticalSection() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- enterModelCriticalSection() > The Model couldn't be locked.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}

	protected void leaveModelCriticalSection(Model domainModel) throws CarbonException {
		try {
			domainModel.leaveCriticalSection();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx enterModelCriticalSection() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- enterModelCriticalSection() > The Model couldn't be locked.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}
}
