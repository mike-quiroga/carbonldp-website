package com.base22.carbon.sparql;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.repository.services.RepositoryService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

@Service("sparqlService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class SPARQLService {

	@Autowired
	private RepositoryService repositoryService;

	static final Logger LOG = LoggerFactory.getLogger(SPARQLService.class);

	public static enum Verb {
		SELECT, CONSTRUCT, DESCRIBE, ASK
	}

	private HashMap<ResultSet, QueryExecution> queryExecutions;

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> init()");
		}
	}

	public Query createQuery(String queryString) throws CarbonException {
		return createQuery(queryString, false);
	}

	public Query createQuery(String queryString, boolean exposeErrors) throws CarbonException {
		Query query = null;
		try {
			query = QueryFactory.create(queryString);
		} catch (QueryParseException e) {
			if ( exposeErrors ) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("-- createQuery() > The SPARQL Query: \n{}\n, couldn't be parsed.", queryString);
				}

				String friendlyMessage = "The SPARQL Query isn't valid.";
				String debugMessage = e.getMessage();

				ErrorResponseFactory errorFactory = new ErrorResponseFactory();
				ErrorResponse errorObject = errorFactory.create();
				errorObject.setFriendlyMessage(friendlyMessage);
				errorObject.setDebugMessage(debugMessage);
				throw new CarbonException(errorObject);
			} else {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx createQuery() > Exception Stacktrace:", e);
				}

				if ( LOG.isErrorEnabled() ) {
					LOG.error("-- createQuery() > The SPARQL Query: \n{}\n, couldn't be parsed.", queryString);
				}

				String friendlyMessage = "An unexpected exception ocurred.";

				ErrorResponseFactory errorFactory = new ErrorResponseFactory();
				ErrorResponse errorObject = errorFactory.create();
				errorObject.setFriendlyMessage(friendlyMessage);
				throw new CarbonException(errorObject);
			}
		}
		return query;
	}

	public Verb getQueryVerb(Query query) throws CarbonException {
		if ( query.isSelectType() ) {
			return Verb.SELECT;
		} else if ( query.isAskType() ) {
			return Verb.ASK;
		} else if ( query.isDescribeType() ) {
			return Verb.DESCRIBE;
		} else if ( query.isConstructType() ) {
			return Verb.CONSTRUCT;
		} else {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- getQueryVerb() > The SPARQL Query: '{}', has a verb not supported.", query.toString());
			}

			String friendlyMessage = "An unexpected error ocurred.";
			String debugMessage = "A SPARQL Query contains an unknown verb and cannot be completed.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			throw new CarbonException(errorObject);
		}
	}

	public ResultSet select(String sparqlQuery, String dataset) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return select(query, dataset);
	}

	public ResultSet select(Query query, String dataset) throws CarbonException {
		// TODO: Implement
		return null;
	}

	public ResultSet select(String sparqlQuery, Model domainModel) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return select(query, domainModel);
	}

	public ResultSet select(Query query, Model domainModel) throws CarbonException {
		ResultSet resultSet = null;

		if ( ! query.isSelectType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- select() > Trying to execute non SELECT query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		QueryExecution execution = null;
		try {
			execution = QueryExecutionFactory.create(query, domainModel);
			resultSet = execution.execSelect();
			resultSet = ResultSetFactory.copyResults(resultSet);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx select() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- select() > The SPARQL Query: \n{}\n, couldn't be executed.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		return resultSet;
	}

	public ResultSet select(SPARQLQuery sparqlQuery) throws RepositoryServiceException, SPARQLQueryException {
		ResultSet resultSet = null;
		Dataset dataset = null;
		Query query = null;
		QueryExecution queryExecution = null;

		dataset = repositoryService.getDataset(sparqlQuery.getDataset());
		if ( dataset.supportsTransactions() ) {
			try {
				dataset.begin(ReadWrite.READ);
			} catch (Exception exception) {
				LOG.error("The dataset couldn't be started.");
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				throw new RepositoryServiceException("The dataset couldn't be started.");
			}
		}

		try {
			query = QueryFactory.create(sparqlQuery.getQuery());
		} catch (Exception exception) {
			String message = "The sparql query couldn't be parsed. Reason: " + exception.getMessage();
			LOG.error(message);
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new SPARQLQueryException(sparqlQuery, message);
		}

		try {
			queryExecution = QueryExecutionFactory.create(query, dataset);
			resultSet = queryExecution.execSelect();
		} catch (Exception exception) {
			LOG.error("Trying to execute non SELECT query in the select query method. Query: {}", sparqlQuery.getQuery());
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new SPARQLQueryException(sparqlQuery, "Trying to execute non SELECT query in the select query method.");
		} finally {
			// End the transaction (if supported)
			if ( dataset.supportsTransactions() ) {
				try {
					dataset.end();
				} catch (Exception exception) {
					LOG.error("The transaction under the dataset couldn't be finished.");
					if ( LOG.isDebugEnabled() ) {
						exception.printStackTrace();
					}
					throw new RepositoryServiceException("The transaction under the dataset couldn't be finished.");
				}
			}
		}

		// Add the resultSet to the hashMap so the related queryExecution object can be closed later on
		if ( queryExecutions == null ) {
			queryExecutions = new HashMap<ResultSet, QueryExecution>();
		}
		queryExecutions.put(resultSet, queryExecution);

		return resultSet;
	}

	/**
	 * Execute a construct query
	 * 
	 * @param sparqlQuery
	 *            The sparql query to be executed
	 * @return a Model representing the result of the construct query.
	 * @throws SPARQLQueryException
	 * @throws RepositoryServiceException
	 */
	public Model construct(SPARQLQuery sparqlQuery) throws SPARQLQueryException, RepositoryServiceException {

		Model resultModel = null;
		Dataset dataset = null;
		Query query = null;
		QueryExecution queryExecution = null;

		dataset = repositoryService.getDataset(sparqlQuery.getDataset());

		if ( dataset.supportsTransactions() ) {
			try {
				dataset.begin(ReadWrite.READ);
			} catch (Exception exception) {
				LOG.error("The dataset couldn't be started.");
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				throw new RepositoryServiceException("The dataset couldn't be started.");
			}
		}

		try {
			try {
				query = QueryFactory.create(sparqlQuery.getQuery());
				queryExecution = QueryExecutionFactory.create(query, dataset);
			} catch (Exception exception) {
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				throw new SPARQLQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
			try {

				resultModel = queryExecution.execConstruct();
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "Trying to execute non CONSTRUCT query in the construct query method.");
			}
		} finally {
			// Try to close the queryExecution object
			closeQueryExecution(queryExecution);

			if ( dataset.supportsTransactions() ) {
				try {
					dataset.end();
				} catch (Exception exception) {
					if ( LOG.isDebugEnabled() ) {
						exception.printStackTrace();
					}
				}
			}
		}

		return resultModel;
	}

	/**
	 * Execute a describe query
	 * 
	 * @param sparqlQuery
	 *            The sparql query to be executed
	 * @return a Model representing the result of the describe query.
	 * @throws SPARQLQueryException
	 * @throws RepositoryServiceException
	 */
	public Model describe(SPARQLQuery sparqlQuery) throws SPARQLQueryException, RepositoryServiceException {
		Model resultModel = null;
		Dataset dataset = null;
		Query query = null;
		QueryExecution queryExecution = null;

		dataset = repositoryService.getDataset(sparqlQuery.getDataset());

		if ( dataset.supportsTransactions() ) {
			try {
				dataset.begin(ReadWrite.READ);
			} catch (Exception exception) {
				LOG.error("The dataset couldn't be started.");
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				throw new RepositoryServiceException("The dataset couldn't be started.");
			}
		}

		try {
			try {
				query = QueryFactory.create(sparqlQuery.getQuery());
				queryExecution = QueryExecutionFactory.create(query, dataset);
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
			try {
				resultModel = queryExecution.execDescribe();
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "Trying to execute non DESCRIBE query in the describe query method.");
			}
		} finally {
			// Try to close the queryExecution object
			closeQueryExecution(queryExecution);

			if ( dataset.supportsTransactions() ) {
				try {
					dataset.end();
				} catch (Exception exception) {
					if ( LOG.isDebugEnabled() ) {
						exception.printStackTrace();
					}
				}
			}
		}

		return resultModel;
	}

	/**
	 * Execute an ask query
	 * 
	 * @param sparqlQuery
	 *            The sparql query to be executed
	 * @return a boolean result of the query asked.
	 * @throws SPARQLQueryException
	 * @throws RepositoryServiceException
	 */
	public boolean ask(SPARQLQuery sparqlQuery) throws SPARQLQueryException, RepositoryServiceException {
		boolean result;
		Dataset dataset = null;
		Query query = null;
		QueryExecution queryExecution = null;

		dataset = repositoryService.getDataset(sparqlQuery.getDataset());

		if ( dataset.supportsTransactions() ) {
			try {
				dataset.begin(ReadWrite.READ);
			} catch (Exception exception) {
				LOG.error("The dataset couldn't be started.");
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				throw new RepositoryServiceException("The dataset couldn't be started.");
			}
		}

		try {
			try {
				query = QueryFactory.create(sparqlQuery.getQuery());
				queryExecution = QueryExecutionFactory.create(query, dataset);
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
			try {
				result = queryExecution.execAsk();
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "Trying to execute non ASK query in the ask query method.");
			}
		} finally {
			// Try to close the queryExecution object
			closeQueryExecution(queryExecution);

			if ( dataset.supportsTransactions() ) {
				try {
					dataset.end();
				} catch (Exception exception) {
					if ( LOG.isDebugEnabled() ) {
						exception.printStackTrace();
					}
				}
			}
		}

		return result;
	}

	/**
	 * Execute an update query.
	 * 
	 * @param sparqlQuery
	 *            The sparql query to be executed.
	 * @return a boolean that represents if the query has been executed successfully
	 */
	public boolean update(SPARQLQuery sparqlQuery) throws SPARQLQueryException, RepositoryServiceException {
		boolean executed = false;
		Dataset dataset = null;
		UpdateRequest update = null;

		dataset = repositoryService.getDataset(sparqlQuery.getDataset());

		if ( dataset.supportsTransactions() ) {
			try {
				dataset.begin(ReadWrite.WRITE);
			} catch (Exception exception) {
				LOG.error("The dataset couldn't be started.");
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				throw new RepositoryServiceException("The dataset couldn't be started.");
			}
		}

		try {
			try {
				update = UpdateFactory.create(sparqlQuery.getQuery());
				UpdateAction.execute(update, dataset);
				executed = true;
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
		} finally {
			if ( dataset.supportsTransactions() ) {
				try {
					dataset.commit();
				} catch (Exception exception) {
					if ( LOG.isDebugEnabled() ) {
						exception.printStackTrace();
					}
				}
				try {
					dataset.end();
				} catch (Exception exception) {
					if ( LOG.isDebugEnabled() ) {
						exception.printStackTrace();
					}
				}
			}
		}

		return executed;
	}

	/**
	 * Execute an update query.
	 * 
	 * @param sparqlQuery
	 *            The sparql query to be executed.
	 * @return a boolean that represents if the query has been executed successfully
	 */
	public boolean update(SPARQLQuery sparqlQuery, String modelName) throws SPARQLQueryException, RepositoryServiceException {
		boolean executed = false;
		Model namedModel = null;
		UpdateRequest update = null;

		namedModel = repositoryService.getNamedModel(modelName, sparqlQuery.getDataset());

		if ( namedModel.size() == 0 ) {
			// Named model doesn't exist
			return executed;
		}

		try {
			namedModel.enterCriticalSection(Lock.WRITE);
		} catch (Exception exception) {
			LOG.error("The named Model couldn't be started.");
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The named Model couldn't be started.");
		}

		try {
			try {
				update = UpdateFactory.create(sparqlQuery.getQuery());
				UpdateAction.execute(update, namedModel);
				executed = true;
			} catch (Exception exception) {
				throw new SPARQLQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
		} finally {
			try {
				namedModel.commit();
			} catch (Exception exception) {
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
			}
			try {
				namedModel.leaveCriticalSection();
			} catch (Exception exception) {
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
			}
		}

		return executed;
	}

	public void closeResultSet(ResultSet resultSet) {
		if ( queryExecutions == null ) {
			return;
		}
		if ( ! queryExecutions.containsKey(resultSet) ) {
			return;
		}
		closeQueryExecution(queryExecutions.get(resultSet));
	}

	private void closeQueryExecution(QueryExecution queryExecution) {
		try {
			queryExecution.close();
		} catch (Exception exception) {
			if ( LOG.isErrorEnabled() ) {
				exception.printStackTrace();
			}
		}
	}

	// Autowired setter methods, for testing purposes only
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

}
