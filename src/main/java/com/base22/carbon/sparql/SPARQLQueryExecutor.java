package com.base22.carbon.sparql;

import com.base22.carbon.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.sparql.SPARQLService.Verb;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;

public class SPARQLQueryExecutor<T> extends SPARQLExecutor {

	protected final Verb queryVerb;
	protected final RepositoryService repositoryService;

	public SPARQLQueryExecutor(Verb queryVerb, RepositoryService repositoryService) {
		this.queryVerb = queryVerb;
		this.repositoryService = repositoryService;
	}

	public T execute(Query query, Model domainModel) throws CarbonException {

		this.enterModelCriticalSection(domainModel, Lock.READ);

		QueryExecution queryExecution = this.getQueryExecution(query, domainModel);

		T result = null;
		try {
			result = this.executeQuery(queryExecution);
		} finally {
			try {
				this.closeQueryExecution(queryExecution);
			} finally {
				this.leaveModelCriticalSection(domainModel);
			}
		}

		return result;
	}

	public T execute(Query query, String datasetName) throws CarbonException {

		Dataset dataset = this.getDataset(datasetName, this.repositoryService);

		if ( dataset.supportsTransactions() ) {
			this.beginDatasetTransaction(datasetName, dataset, ReadWrite.READ);
		}

		QueryExecution queryExecution = null;
		T result = null;
		try {
			queryExecution = this.getQueryExecution(query, dataset);
			try {
				result = this.executeQuery(queryExecution);
			} finally {
				this.closeQueryExecution(queryExecution);
			}
		} finally {
			if ( dataset.supportsTransactions() ) {
				this.endDatasetTransaction(datasetName, dataset);
			}
		}

		return result;
	}

	private QueryExecution getQueryExecution(Query query, Model domainModel) throws CarbonException {
		QueryExecution queryExecution = null;
		try {
			queryExecution = QueryExecutionFactory.create(query, domainModel);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getQueryExecution() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- getQueryExecution() > The queryExecution for the query: \n{}\n couldn't be created.", query.toString());
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
		return queryExecution;
	}

	protected QueryExecution getQueryExecution(Query query, Dataset dataset) throws CarbonException {
		QueryExecution queryExecution = null;
		try {
			queryExecution = QueryExecutionFactory.create(query, dataset);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getQueryExecution() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- getQueryExecution() > The queryExecution for the query: \n{}\n couldn't be created.", query.toString());
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
		return queryExecution;
	}

	@SuppressWarnings("unchecked")
	private T executeQuery(QueryExecution queryExecution) throws CarbonException {
		T result = null;

		try {
			switch (this.queryVerb) {
				case ASK:
					result = (T) Boolean.valueOf(queryExecution.execAsk());
					break;
				case CONSTRUCT:
					result = (T) queryExecution.execConstruct();
					break;
				case DESCRIBE:
					result = (T) queryExecution.execDescribe();
					break;
				case SELECT:
					result = (T) queryExecution.execSelect();
					result = (T) ResultSetFactory.copyResults((ResultSet) result);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeQuery() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- executeQuery() > The queryExecution couldn't be executed.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		return result;
	}

	private void closeQueryExecution(QueryExecution queryExecution) throws CarbonException {
		try {
			queryExecution.close();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx closeQueryExecution() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- closeQueryExecution() > The queryExecution couldn't be closed.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

	}
}
