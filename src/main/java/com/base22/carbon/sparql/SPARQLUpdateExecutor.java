package com.base22.carbon.sparql;

import com.base22.carbon.CarbonException;
import com.base22.carbon.jdbc.TransactionException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.repository.services.TransactionNamedModelCache;
import com.base22.carbon.repository.services.WriteTransactionCallback;
import com.base22.carbon.repository.services.WriteTransactionTemplate;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateRequest;

public class SPARQLUpdateExecutor extends SPARQLExecutor {

	protected final RepositoryService repositoryService;

	public SPARQLUpdateExecutor(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void execute(final UpdateRequest updateRequest, String datasetName, final String namedModelName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		//@formatter:off
		template.execute(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
				Model namedModel = null;
				try {
					namedModel = namedModelCache.getNamedModel(namedModelName);
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx execute() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< execute() > The named model: '{}', couldn't be retrieved.", namedModelName);
					}
					throw new TransactionException("The named model couldn't be retrieved.");
				}
				execute(updateRequest, namedModel);
			}
		});
	}

	public void execute(UpdateRequest updateRequest, Model domainModel) throws CarbonException {
		if ( domainModel.supportsTransactions() ) {
			this.enterModelCriticalSection(domainModel, Lock.WRITE);
		}

		try {
			this.executeUpdate(updateRequest, domainModel);
		} finally {
			try {
				if ( domainModel.supportsTransactions() ) {
					this.commitModel(domainModel);
				}
			} finally {
				if ( domainModel.supportsTransactions() ) {
					this.leaveModelCriticalSection(domainModel);
				}
			}
		}
	}

	public void execute(final UpdateRequest updateRequest, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		//@formatter:off
		template.execute(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
				executeUpdate(updateRequest, dataset);
			}
		});
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
