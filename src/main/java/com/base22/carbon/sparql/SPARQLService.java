package com.base22.carbon.sparql;

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
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
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
		//@formatter:off
		SELECT(ResultSet.class), 
		CONSTRUCT(Model.class), 
		DESCRIBE(Model.class), 
		ASK(Boolean.class);
		//@formatter:on

		public final Class<?> resultClass;

		Verb(Class<?> resultClass) {
			this.resultClass = resultClass;
		}
	}

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

	public ResultSet select(Query query, String datasetName) throws CarbonException {
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

		SPARQLQueryExecutor<ResultSet> executor = new SPARQLQueryExecutor<ResultSet>(Verb.SELECT, this.repositoryService);
		return executor.execute(query, datasetName);
	}

	public ResultSet select(String sparqlQuery, Model domainModel) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return select(query, domainModel);
	}

	public ResultSet select(Query query, Model domainModel) throws CarbonException {
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

		SPARQLQueryExecutor<ResultSet> executor = new SPARQLQueryExecutor<ResultSet>(Verb.SELECT, this.repositoryService);
		return executor.execute(query, domainModel);
	}

	public Model construct(String sparqlQuery, String dataset) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return construct(query, dataset);
	}

	public Model construct(Query query, String datasetName) throws CarbonException {
		if ( ! query.isConstructType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- construct() > Trying to execute non CONSTRUCT query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		SPARQLQueryExecutor<Model> executor = new SPARQLQueryExecutor<Model>(Verb.CONSTRUCT, this.repositoryService);
		return executor.execute(query, datasetName);
	}

	public Model construct(String sparqlQuery, Model domainModel) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return construct(query, domainModel);
	}

	public Model construct(Query query, Model domainModel) throws CarbonException {
		if ( ! query.isConstructType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- construct() > Trying to execute non CONSTRUCT query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		SPARQLQueryExecutor<Model> executor = new SPARQLQueryExecutor<Model>(Verb.CONSTRUCT, this.repositoryService);
		return executor.execute(query, domainModel);
	}

	public Model describe(String sparqlQuery, String dataset) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return construct(query, dataset);
	}

	public Model describe(Query query, String datasetName) throws CarbonException {
		if ( ! query.isDescribeType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- describe() > Trying to execute non DESCRIBE query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		SPARQLQueryExecutor<Model> executor = new SPARQLQueryExecutor<Model>(Verb.DESCRIBE, this.repositoryService);
		return executor.execute(query, datasetName);
	}

	public Model describe(String sparqlQuery, Model domainModel) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return construct(query, domainModel);
	}

	public Model describe(Query query, Model domainModel) throws CarbonException {
		if ( ! query.isDescribeType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- describe() > Trying to execute non DESCRIBE query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		SPARQLQueryExecutor<Model> executor = new SPARQLQueryExecutor<Model>(Verb.DESCRIBE, this.repositoryService);
		return executor.execute(query, domainModel);
	}

	public Boolean ask(String sparqlQuery, String dataset) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return ask(query, dataset);
	}

	public Boolean ask(Query query, String datasetName) throws CarbonException {
		if ( ! query.isAskType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- ask() > Trying to execute non ASK query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		SPARQLQueryExecutor<Boolean> executor = new SPARQLQueryExecutor<Boolean>(Verb.ASK, this.repositoryService);
		return executor.execute(query, datasetName);
	}

	public Boolean ask(String sparqlQuery, Model domainModel) throws CarbonException {
		Query query = createQuery(sparqlQuery);
		return ask(query, domainModel);
	}

	public Boolean ask(Query query, Model domainModel) throws CarbonException {
		if ( ! query.isAskType() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- ask() > Trying to execute non ASK query in the select query method. Query: \n{}.", query.toString());
			}

			String friendlyMessage = "An unexpected exception ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}

		SPARQLQueryExecutor<Boolean> executor = new SPARQLQueryExecutor<Boolean>(Verb.ASK, this.repositoryService);
		return executor.execute(query, domainModel);
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

	// Autowired setter methods, for testing purposes only
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

}
