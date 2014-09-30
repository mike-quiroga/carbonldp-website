package com.base22.carbon.repository.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.repository.ReadTransactionCallback;
import com.base22.carbon.repository.ReadTransactionTemplate;
import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.repository.TransactionNamedModelCache;
import com.base22.carbon.repository.WriteTransactionCallback;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.base22.carbon.sparql.SPARQLService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

@Service("modelService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ModelService {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private SPARQLService sparqlService;

	static final Logger LOG = LoggerFactory.getLogger(ModelService.class);

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> init()");
		}
	}

	public void addStatements(final URIObject uriObject, final List<Statement> statements, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		addStatements(uriObject, statements, template);
		template.execute();
	}

	public void addStatements(final URIObject uriObject, final List<Statement> statements, WriteTransactionTemplate template) throws CarbonException {
		//@formatter:off
		template.addCallback(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
				Model model = namedModelCache.getNamedModel(uriObject.getURI());
				model.add(statements);
			}
		});
	}

	public void deleteStatements(final URIObject uriObject, final List<Statement> statements, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		deleteStatements(uriObject, statements, template);
		template.execute();
	}

	public void deleteStatements(final URIObject uriObject, final List<Statement> statements, WriteTransactionTemplate template) throws CarbonException {
		//@formatter:off
		template.addCallback(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
				Model model = namedModelCache.getNamedModel(uriObject.getURI());
				model.remove(statements);
			}
		});
	}

	public void deleteProperty(final URIObject uriObject, final String subjectURI, final Property property, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		deleteProperty(uriObject, subjectURI, property, template);
		template.execute();
	}

	public void deleteProperty(final URIObject uriObject, final String subjectURI, final Property property, WriteTransactionTemplate template)
			throws CarbonException {
		template.addCallback(new WriteTransactionCallback() {
			// @formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
				Model model = namedModelCache.getNamedModel(uriObject.getURI());
				Resource resource = model.getResource(subjectURI);
				resource.removeAll(property);
			}
		});
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
		addNamedModel(modelName, model, template);
		template.execute();
	}

	public void addNamedModel(final String modelName, final Model model, WriteTransactionTemplate template) throws CarbonException {
		//@formatter:off
		template.addCallback(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
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
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
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

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setSparqlService(SPARQLService sparqlService) {
		this.sparqlService = sparqlService;
	}

}
