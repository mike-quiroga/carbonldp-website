package com.base22.carbon.ldp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.base22.carbon.AbstractService;
import com.base22.carbon.CarbonException;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.ldp.patch.PATCHService;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.base22.carbon.repository.services.ModelService;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.sparql.SPARQLService;
import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractLDPService extends AbstractService {

	@Autowired
	protected PATCHService patchService;

	@Autowired
	protected SPARQLService sparqlService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected URIObjectDAO uriObjectDAO;

	@Autowired
	protected PermissionService permissionService;

	protected List<URIObject> createChildRDFSources(List<? extends RDFSource> rdfSources, URIObject parentURIObject, WriteTransactionTemplate template)
			throws CarbonException {
		List<URIObject> uriObjects = new ArrayList<URIObject>();

		for (RDFSource rdfSource : rdfSources) {
			String rdfSourceURI = rdfSource.getURI();

			URIObject uriObject = new URIObject(rdfSourceURI);
			uriObjects.add(uriObject);

			Model namedModel = rdfSource.getResource().getModel();
			try {
				modelService.addNamedModel(rdfSourceURI, namedModel, template);
			} catch (CarbonException e) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< createChildRDFSources() > The URIObject for the child couldn't be created.");
				}
				throw e;
			}
		}

		try {
			uriObjects = uriObjectDAO.createURIObjects(uriObjects);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createChildRDFSources() > The URIObjects for the children couldn't be created.");
			}
			throw e;
		}

		try {
			permissionService.setParentToMany(uriObjects, parentURIObject);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createChildRDFSources() > Couldn't set the parent of the RDFSources URIObjects.");
			}
			throw e;
		}

		return uriObjects;
	}

	protected URIObject createChildRDFSource(RDFSource rdfSource, URIObject parentURIObject, String dataset) throws CarbonException {
		String documentURI = rdfSource.getURI();

		// Create the URIObject for the LDPRSource
		URIObject documentURIObject = new URIObject(documentURI);
		try {
			documentURIObject = uriObjectDAO.createURIObject(documentURIObject);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createChildRDFSource() > The URIObject for the child couldn't be created.");
			}
			throw e;
		}

		// Add the namedModel to the dataset
		Model sourceModel = rdfSource.getResource().getModel();
		try {
			modelService.addNamedModel(documentURI, sourceModel, dataset);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createChildRDFSource() > The model for the child couldn't be created.");
			}
			throw e;
		}

		try {
			permissionService.setParent(documentURIObject, parentURIObject);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createChildRDFSource() > Couldn't set the parent of the child's URIObject.");
			}
			throw e;
		}

		return documentURIObject;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public void setSparqlService(SPARQLService sparqlService) {
		this.sparqlService = sparqlService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setPatchService(PATCHService patchService) {
		this.patchService = patchService;
	}

	public void setUriObjectDAO(URIObjectDAO uriObjectDAO) {
		this.uriObjectDAO = uriObjectDAO;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
