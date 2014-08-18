package com.base22.carbon.services;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.base22.carbon.exceptions.RepositoryServiceException;
import com.base22.carbon.exceptions.SparqlQueryException;
import com.base22.carbon.models.SparqlQuery;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

@Service("sparqlService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class SparqlService {

	@Autowired
	private RepositoryService repositoryService;

	static final Logger LOG = LoggerFactory.getLogger(SparqlService.class);

	private HashMap<ResultSet, QueryExecution> queryExecutions;

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> init()");
		}
	}

	/**
	 * Validates a sparql query.
	 * 
	 * @param sparqlQuery
	 *            The sparql query to validate.
	 * 
	 * @return true if the whole query is correct (type and syntax), false if the type isn't supported and an exception
	 *         if the query's syntax is incorrect.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public boolean validate(SparqlQuery sparqlQuery) throws Exception {
		switch (sparqlQuery.getType()) {
			case QUERY:
				Query query = QueryFactory.create(sparqlQuery.getQuery());
				return true;
			case UPDATE:
				UpdateRequest update = UpdateFactory.create(sparqlQuery.getQuery());
				return true;
			default:
				return false;
		}
	}

	/**
	 * Execute a select query
	 * 
	 * @param sparqlQuery
	 *            The sparql query to be executed
	 * @return a ResultSet containing the results of the select query
	 */
	public ResultSet select(SparqlQuery sparqlQuery) throws RepositoryServiceException, SparqlQueryException {
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
			throw new SparqlQueryException(sparqlQuery, message);
		}

		try {
			queryExecution = QueryExecutionFactory.create(query, dataset);
			resultSet = queryExecution.execSelect();
		} catch (Exception exception) {
			LOG.error("Trying to execute non SELECT query in the select query method. Query: {}", sparqlQuery.getQuery());
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new SparqlQueryException(sparqlQuery, "Trying to execute non SELECT query in the select query method.");
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
	 * @throws SparqlQueryException
	 * @throws RepositoryServiceException
	 */
	public Model construct(SparqlQuery sparqlQuery) throws SparqlQueryException, RepositoryServiceException {

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
				throw new SparqlQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
			try {

				resultModel = queryExecution.execConstruct();
			} catch (Exception exception) {
				throw new SparqlQueryException(sparqlQuery, "Trying to execute non CONSTRUCT query in the construct query method.");
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
	 * @throws SparqlQueryException
	 * @throws RepositoryServiceException
	 */
	public Model describe(SparqlQuery sparqlQuery) throws SparqlQueryException, RepositoryServiceException {
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
				throw new SparqlQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
			try {
				resultModel = queryExecution.execDescribe();
			} catch (Exception exception) {
				throw new SparqlQueryException(sparqlQuery, "Trying to execute non DESCRIBE query in the describe query method.");
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
	 * @throws SparqlQueryException
	 * @throws RepositoryServiceException
	 */
	public boolean ask(SparqlQuery sparqlQuery) throws SparqlQueryException, RepositoryServiceException {
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
				throw new SparqlQueryException(sparqlQuery, "The query couldn't be parsed.");
			}
			try {
				result = queryExecution.execAsk();
			} catch (Exception exception) {
				throw new SparqlQueryException(sparqlQuery, "Trying to execute non ASK query in the ask query method.");
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
	public boolean update(SparqlQuery sparqlQuery) throws SparqlQueryException, RepositoryServiceException {
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
				throw new SparqlQueryException(sparqlQuery, "The query couldn't be parsed.");
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
	public boolean update(SparqlQuery sparqlQuery, String modelName) throws SparqlQueryException, RepositoryServiceException {
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
				throw new SparqlQueryException(sparqlQuery, "The query couldn't be parsed.");
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
