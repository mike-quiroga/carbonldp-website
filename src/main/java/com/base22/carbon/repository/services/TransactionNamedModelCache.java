package com.base22.carbon.repository.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.base22.carbon.CarbonException;
import com.base22.carbon.jdbc.TransactionException;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

public class TransactionNamedModelCache {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final Dataset dataset;
	private Map<String, Model> namedModels;

	public TransactionNamedModelCache(Dataset dataset) {
		this.dataset = dataset;
		namedModels = new HashMap<String, Model>();
	}

	public Model getNamedModel(String name) throws CarbonException {
		Model namedModel = null;
		if ( namedModels.containsKey(name) ) {
			namedModel = namedModels.get(name);
		} else {
			try {
				namedModel = dataset.getNamedModel(name);
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx getNamedModel() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< getNamedModel() > The named model: '{}', couldn't be retrieved.", name);
				}
				throw new TransactionException("The named model couldn't be retrieved.");
			}
			namedModels.put(name, namedModel);
		}
		return namedModel;
	}
}
