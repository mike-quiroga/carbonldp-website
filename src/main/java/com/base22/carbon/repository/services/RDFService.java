package com.base22.carbon.repository.services;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.sparql.SPARQLService;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

@Service("rdfService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RDFService {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private SPARQLService sparqlService;

	static final Logger LOG = LoggerFactory.getLogger(RDFService.class);

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> init()");
		}
	}

	public void insertTriple(String documentName, String subject, String predicate, Object object, String dataset) throws CarbonException {
		String query = "INSERT DATA { <" + subject + "> <" + predicate + "> " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: " + query);

		sparqlService.update(query, documentName, dataset);
	}

	public void insertTriple(String subject, String predicate, String object, String typeOfObject, String dataset) throws CarbonException {
		String query = "INSERT DATA { <" + subject + "> <" + predicate + "> \"" + object + "\"^^" + typeOfObject + " }";
		LOG.trace(">> query: " + query);

		sparqlService.update(query, dataset);
	}

	public void deleteTriples(String documentName, String subject, String predicate, Object object, String dataset) throws CarbonException {
		subject = (subject == null) ? "?s" : subject;
		predicate = (predicate == null) ? "?p" : "<" + predicate + ">";
		object = (object == null) ? "?o" : object;

		String query = "DELETE WHERE { <" + subject + "> " + predicate + " " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: " + query);

		sparqlService.update(query, documentName, dataset);
	}

	public boolean triplesExist(String subject, String predicate, Object object, String dataset) throws CarbonException {
		boolean exists = false;

		subject = (subject == null) ? "?s" : subject;
		predicate = (predicate == null) ? "?p" : "<" + predicate + ">";
		object = (object == null) ? "?o" : object;

		String query = "ASK { <" + subject + "> " + predicate + " " + createTypedLiteral(object) + " }";
		LOG.trace(">> query: " + query);

		exists = sparqlService.ask(query, dataset);

		return exists;
	}

	public boolean resourceExists(String uri, String dataset) throws CarbonException {
		return this.triplesExist(uri, null, null, dataset);
	}

	public Model getNamedModel(final String name, String datasetName) throws CarbonException {
		Model model = null;

		ReadTransactionTemplate<Model> template = repositoryService.getReadTransactionTemplate(datasetName);
		//@formatter:off
		model = template.execute(new ReadTransactionCallback<Model>() {
			//@formatter:on
			@Override
			public Model executeInTransaction(Dataset dataset) throws Exception {
				try {
					return dataset.getNamedModel(name);
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx getNamedModel() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< getNamedModel() > The named model: '{}', couldn't be retrieved.", name);
					}
					throw new RepositoryServiceException("The named model couldn't be retrieved.");
				}
			}
		});

		return model;
	}

	public void addNamedModel(final String modelName, final Model model, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		//@formatter:off
		template.execute(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset) throws Exception {
				// TODO Auto-generated method stub
				try {
					dataset.addNamedModel(modelName, model);
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx addNamedModel() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< addNamedModel() > The named model: '{}', couldn't be added.", modelName);
					}
				}
			}
		});
	}

	public boolean namedModelExists(final String name, String datasetName) throws CarbonException {
		Boolean exists = null;

		ReadTransactionTemplate<Boolean> template = repositoryService.getReadTransactionTemplate(datasetName);
		//@formatter:off
		exists = template.execute(new ReadTransactionCallback<Boolean>() {
			//@formatter:on
			@Override
			public Boolean executeInTransaction(Dataset dataset) throws Exception {
				try {
					return Boolean.valueOf(dataset.containsNamedModel(name));
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx namedModelExists() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< namedModelExists() > The named model: '{}', couldn't checked for existence.", name);
					}
					throw new RepositoryServiceException("The named model couldn't checked for existence.");
				}
			}
		});

		return exists;
	}

	public void deleteNamedModel(final String modelName, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		//@formatter:off
		template.execute(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset) throws Exception {
				try {
					dataset.removeNamedModel(modelName);
				} catch (Exception e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx deleteNamedModel() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< deleteNamedModel() > The named model: '{}', couldn't be deleted.", modelName);
					}
				}
			}
		});
	}

	// ==== End: Model Related Methods

	public static String createTypedLiteral(Object object) {
		// Try to guess the type of the object
		if ( object instanceof String ) {
			// It is a string, so check if it is a valid resource URI
			if ( HTTPUtil.isValidURL((String) object) ) {
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

	public void setSparqlService(SPARQLService sparqlService) {
		this.sparqlService = sparqlService;
	}

}
