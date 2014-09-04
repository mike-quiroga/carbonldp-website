package com.base22.carbon.sparql;

import com.base22.carbon.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.services.RepositoryService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class SPARQLUpdateExecutor extends SPARQLExecutor {

	protected final RepositoryService repositoryService;

	public SPARQLUpdateExecutor(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void execute(String updateString, String datasetName, String namedModelName) throws CarbonException {
		Model domainModel = this.getDomainModel(namedModelName, datasetName);
		execute(updateString, domainModel);
	}

	public void execute(String updateString, Model domainModel) throws CarbonException {
		if ( domainModel.supportsTransactions() ) {
			this.enterModelCriticalSection(domainModel, Lock.WRITE);
		}

		UpdateRequest updateRequest = null;

		try {
			updateRequest = this.getUpdateRequest(updateString);
			try {
				this.executeUpdate(updateRequest, domainModel);
			} finally {
				if ( domainModel.supportsTransactions() ) {
					this.commitModel(domainModel);
				}
			}
		} finally {
			if ( domainModel.supportsTransactions() ) {
				this.leaveModelCriticalSection(domainModel);
			}
		}
	}

	public void execute(String updateString, String datasetName) throws CarbonException {
		Dataset dataset = this.getDataset(datasetName, this.repositoryService);

		if ( dataset.supportsTransactions() ) {
			this.beginDatasetTransaction(datasetName, dataset, ReadWrite.READ);
		}

		UpdateRequest updateRequest = null;
		try {
			updateRequest = this.getUpdateRequest(updateString);
			try {
				this.executeUpdate(updateRequest, dataset);
			} finally {
				if ( dataset.supportsTransactions() ) {
					this.commitDataset(datasetName, dataset);
				}
			}
		} finally {
			if ( dataset.supportsTransactions() ) {
				this.endDatasetTransaction(datasetName, dataset);
			}
		}
	}

	private Model getDomainModel(String namedModelName, String datasetName) throws CarbonException {
		Model domainModel = null;
		try {
			domainModel = this.repositoryService.getNamedModel(namedModelName, datasetName);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
		return domainModel;
	}

	private UpdateRequest getUpdateRequest(String updateString) throws CarbonException {
		UpdateRequest updateRequest = null;
		try {
			updateRequest = UpdateFactory.create(updateString);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getUpdateRequest() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- getUpdateRequest() > The updateRequest for the update: \n{}\n couldn't be created.", updateString);
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
		return updateRequest;
	}

	private void executeUpdate(UpdateRequest updateRequest, Dataset dataset) throws CarbonException {
		try {
			UpdateAction.execute(updateRequest, dataset);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeUpdate() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- executeUpdate() > There was a problem executing the update.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}

	private void executeUpdate(UpdateRequest updateRequest, Model domainModel) throws CarbonException {
		try {
			UpdateAction.execute(updateRequest, domainModel);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeUpdate() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- executeUpdate() > There was a problem executing the update.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}

	private void commitDataset(String datasetName, Dataset dataset) throws CarbonException {
		try {
			dataset.commit();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx commitDataset() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- commitDataset() > The dataset: '{}', couldn't be commited.", datasetName);
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

	}

	private void commitModel(Model domainModel) throws CarbonException {
		try {
			domainModel.commit();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx commitModel() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- commitModel() > The model couldn't be commited.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}
}
