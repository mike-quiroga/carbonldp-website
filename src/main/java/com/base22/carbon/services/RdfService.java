package com.base22.carbon.services;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.exceptions.RepositoryServiceException;
import com.base22.carbon.exceptions.SparqlQueryException;
import com.base22.carbon.models.SparqlQuery;
import com.base22.carbon.utils.DatasetTransactionUtil;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;

@Service("rdfService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RdfService {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private SparqlService sparqlService;

	static final Logger LOG = LoggerFactory.getLogger(RdfService.class);

	public void init() {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug(">> init()");
		}
	}

	// Triples, resources, model and named graphs

	/*
	 * ------------------------------------------------------------------ Triples Methods
	 * ------------------------------------------------------------------
	 */

	// TODO: Add security measures to the methods (Prevent SPARQL injection, etc)

	/**
	 * Construct a model with the provided triple from the specified schema's dataset
	 * <p>
	 * 
	 * @param subject
	 *            The subject of the triple(s) to delete. null = wildcard
	 * @param predicate
	 *            The predicate of the triple(s) to delete. null = wildcard
	 * @param object
	 *            The object of the triple(s) to delete. The method tries to guess the type of it. null = wildcard
	 * @param schema
	 *            The schema to use for the deletion
	 * @param dataset
	 *            The dataset from where to delete
	 * @return boolean indicating if the triple was successfully deleted
	 */
	public Model constructWithTriple(String subject, String predicate, Object object, String dataset) throws RepositoryServiceException, SparqlQueryException {
		Model resultModel = null;

		subject = (subject == null) ? "?s" : subject;
		predicate = (predicate == null) ? "?p" : "<" + predicate + ">";
		object = (object == null) ? "?o" : object;

		String query = "CONSTRUCT WHERE { <" + subject + "> " + predicate + " " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: {}", query);
		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.QUERY, dataset, query);

		resultModel = sparqlService.construct(sparqlQuery);

		return resultModel;
	}

	/**
	 * Adds a triple to the specified schema's dataset
	 * <p>
	 * 
	 * @param subject
	 *            The subject of the triple. Must be a valid URI representation.
	 * @param predicate
	 *            The predicate of the triple. Must be a valid URI representation.
	 * @param object
	 *            The object of the triple. The method tries to guess the type of it.
	 * @param schema
	 *            The schema to use for the insertion
	 * @param dataset
	 *            The dataset in where to insert it
	 * @return boolean indicating if the triple was successfully added
	 */
	public boolean insertTriple(String documentName, String subject, String predicate, Object object, String dataset) throws RepositoryServiceException,
			SparqlQueryException {
		boolean executed = false;

		// Make sure that the subject is a valid URI
		if ( ! HttpUtil.isValidURL(subject) || ! HttpUtil.isValidURL(predicate) ) {
			// TODO Handling bad data
			return false;
		}

		String query = "INSERT DATA { <" + subject + "> <" + predicate + "> " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: " + query);
		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.UPDATE, dataset, query);

		executed = sparqlService.update(sparqlQuery, documentName);

		return executed;
	}

	/**
	 * Adds a triple to the specified schema's dataset
	 * <p>
	 * 
	 * @param subject
	 *            The subject of the triple. Must be a valid URI representation.
	 * @param predicate
	 *            The predicate of the triple. Must be a valid URI representation.
	 * @param object
	 *            The object of the triple. The method tries to guess the type of it.
	 * @param schema
	 *            The schema to use for the insertion
	 * @param dataset
	 *            The dataset in where to insert it
	 * @return boolean indicating if the triple was successfully added
	 */
	public boolean insertTriple(String subject, String predicate, String object, String typeOfObject, String dataset) throws RepositoryServiceException,
			SparqlQueryException {
		boolean executed = false;

		// Make sure that the subject is a valid URI
		if ( ! HttpUtil.isValidURL(subject) || ! HttpUtil.isValidURL(predicate) || ! HttpUtil.isValidURL(typeOfObject) ) {
			// TODO Handling bad data
			return false;
		}

		String query = "INSERT DATA { <" + subject + "> <" + predicate + "> \"" + object + "\"^^" + typeOfObject + " }";
		LOG.trace(">> query: " + query);
		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.UPDATE, dataset, query);

		executed = sparqlService.update(sparqlQuery);

		return executed;
	}

	/**
	 * Deletes the triples matching the subject predicate object specified.
	 * <p>
	 * 
	 * @param subject
	 *            The subject of the triple(s) to delete. null = wildcard
	 * @param predicate
	 *            The predicate of the triple(s) to delete. null = wildcard
	 * @param object
	 *            The object of the triple(s) to delete. The method tries to guess the type of it. null = wildcard
	 * @param schema
	 *            The schema to use for the deletion
	 * @param dataset
	 *            The dataset from where to delete
	 * @return boolean indicating if the triple was successfully deleted
	 */
	public boolean deleteTriples(String documentName, String subject, String predicate, Object object, String dataset) throws RepositoryServiceException,
			SparqlQueryException {
		boolean executed = false;

		subject = (subject == null) ? "?s" : subject;
		predicate = (predicate == null) ? "?p" : "<" + predicate + ">";
		object = (object == null) ? "?o" : object;

		String query = "DELETE WHERE { <" + subject + "> " + predicate + " " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: " + query);
		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.UPDATE, dataset, query);

		executed = sparqlService.update(sparqlQuery, documentName);

		return executed;
	}

	/**
	 * Checks if the triple exists in the schema's dataset
	 * <p>
	 * 
	 * @param subject
	 *            The subject of the triple(s) to check for. null = wildcard
	 * @param predicate
	 *            The predicate of the triple(s) to check for. null = wildcard
	 * @param object
	 *            The object of the triple(s) to check for. The method tries to guess the type of it. null = wildcard
	 * @param schema
	 *            The schema to use
	 * @param dataset
	 *            The dataset in where to check
	 * @return boolean indicating if the triple exists
	 */
	public boolean triplesExist(String subject, String predicate, Object object, String dataset) throws RepositoryServiceException, SparqlQueryException {
		boolean exists = false;

		subject = (subject == null) ? "?s" : subject;
		predicate = (predicate == null) ? "?p" : "<" + predicate + ">";
		object = (object == null) ? "?o" : object;

		String query = "ASK { <" + subject + "> " + predicate + " " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: " + query);
		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.QUERY, dataset, query);

		exists = sparqlService.ask(sparqlQuery);

		return exists;
	}

	/*
	 * ------------------------------------------------------------------ Resources Methods
	 * ------------------------------------------------------------------
	 */

	/**
	 * Returns a Model containing the resource which subject matches the URI provided.
	 * <p>
	 * 
	 * @param uri
	 *            The resource URI to search for
	 * @param schema
	 *            The schema were the dataset resides
	 * @param dataset
	 *            The dataset from where the resource will be retrieved
	 * @return a model of the resource that matches the uri
	 */
	public Model getResourceModel(String uri, String dataset) throws RepositoryServiceException, SparqlQueryException {
		return this.constructWithTriple(uri, null, null, dataset);
	}

	/**
	 * Checks if a resource exists in the schema's dataset that matches the provided uri
	 * <p>
	 * 
	 * @return a boolean indicating if the model was successfully added
	 * @param uri
	 *            The resource URI to search for
	 * @param schema
	 *            The schema were the dataset resides
	 * @param dataset
	 *            The dataset where the search will be done
	 */
	public boolean resourceExists(String uri, String dataset) throws RepositoryServiceException, SparqlQueryException {
		return this.triplesExist(uri, null, null, dataset);
	}

	/**
	 * Deletes the resource from the schema's dataset that matches the provided uri
	 * <p>
	 * 
	 * @param uri
	 *            The resource URI
	 * @param schema
	 *            The schema were the dataset resides
	 * @param dataset
	 *            The dataset in where to delete
	 * @return a boolean indicating if the resource was successfully deleted
	 */
	public boolean deleteResource(String uri, String dataset) throws RepositoryServiceException, SparqlQueryException {
		return this.deleteTriples(uri, uri, null, null, dataset);
	}

	// ==== Model Related Methods

	public Model getNamedModel(String name, String dataset) throws RepositoryServiceException {
		return repositoryService.getNamedModel(name, dataset);
	}

	public void addNamedModel(String modelName, Model model, String datasetName) throws CarbonException {
		Dataset dataset = null;

		try {
			dataset = repositoryService.getDataset(datasetName);
		} catch (RepositoryServiceException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addNamedModel() > The dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw e;
		}

		DatasetTransactionUtil.beginDatasetTransaction(dataset, datasetName, ReadWrite.WRITE);

		try {
			dataset.addNamedModel(modelName, model);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx addNamedModel() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addNamedModel() > The named model: '{}', couldn't be added.", modelName);
			}
		} finally {
			DatasetTransactionUtil.closeDatasetTransaction(dataset, datasetName, true);
		}

	}

	public boolean namedModelExists(String name, String datasetName) throws CarbonException {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> namedModelExists(name: '{}', datasetName: '{}')", name, datasetName);
		}

		boolean exists = false;
		Dataset dataset = null;

		try {
			dataset = repositoryService.getDataset(datasetName);
		} catch (RepositoryServiceException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< namedModelExists() > The dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw e;
		}

		DatasetTransactionUtil.beginDatasetTransaction(dataset, datasetName, ReadWrite.READ);

		try {
			exists = dataset.containsNamedModel(name);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx namedModelExists() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< namedModelExists() > The named model: '{}', couldn't be checked for existence.", name);
			}
		} finally {
			DatasetTransactionUtil.closeDatasetTransaction(dataset, datasetName, false);
		}
		return exists;
	}

	public void deleteNamedModel(String name, String datasetName) throws CarbonException {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> deleteNamedModel(name: '{}', datasetName: '{}')", name, datasetName);
		}

		Dataset dataset = null;

		try {
			dataset = repositoryService.getDataset(datasetName);
		} catch (RepositoryServiceException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteNamedModel() > The dataset '{}', couldn't be retrieved.", datasetName);
			}
			throw e;
		}

		DatasetTransactionUtil.beginDatasetTransaction(dataset, datasetName, ReadWrite.WRITE);

		try {
			dataset.removeNamedModel(name);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx deleteNamedModel() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteNamedModel() > The named model: '{}', couldn't be deleted.", name);
			}
		} finally {
			DatasetTransactionUtil.closeDatasetTransaction(dataset, datasetName, true);
		}
	}

	// ==== End: Model Related Methods

	public static String createTypedLiteral(Object object) {
		// Try to guess the type of the object
		if ( object instanceof String ) {
			// It is a string, so check if it is a valid resource URI
			if ( HttpUtil.isValidURL((String) object) ) {
				// It is, add the proper syntax for the query
				object = "<" + (String) object + ">";
			} else if ( ((String) object).startsWith("?") ) {
				// Don't do anything, the object is a wildcard
			} else {
				// It is not a valid URI, so add it as a typed string
				object = "\"" + (String) object + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "string>";
			}
		}
		// Numeric types
		else if ( object instanceof Boolean ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "boolean>";
		} else if ( object instanceof Byte ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "byte>";
		} else if ( object instanceof Integer ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "integer>";
		} else if ( object instanceof Double ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "double>";
		} else if ( object instanceof Short ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "short>";
		} else if ( object instanceof Float ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "float>";
		} else if ( object instanceof Long ) {
			object = "\"" + String.valueOf(object) + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "long>";
		} else if ( object instanceof DateTime ) {
			// Joda DateTime objects get a string representation in ISO8601 which is the used format in SPARQL
			object = "\"" + object.toString() + "\"^^<" + Carbon.CONFIGURED_PREFIXES.get("xsd") + "dateTime>";
		} else {
			object = "\"" + object.toString() + "\"";
		}
		return (String) object;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setSparqlService(SparqlService sparqlService) {
		this.sparqlService = sparqlService;
	}

}
