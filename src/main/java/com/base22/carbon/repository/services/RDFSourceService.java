package com.base22.carbon.repository.services;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.AbstractLDPService;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.RDFSourceFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Service("securedRDFSourceService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RDFSourceService extends AbstractLDPService {

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPRS')")
	public URIObject createChildRDFSource(RDFSource rdfSource, URIObject parentURIObject, String dataset) throws CarbonException {
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

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public RDFSource getRDFSource(URIObject sourceURIObject, String dataset) throws CarbonException {
		String documentURI = sourceURIObject.getURI();

		Model model = modelService.getNamedModel(documentURI, dataset);
		model = model.difference(ModelFactory.createDefaultModel());

		RDFSource rdfSource = null;
		RDFSourceFactory factory = new RDFSourceFactory();
		try {
			rdfSource = factory.create(documentURI, model);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getRDFSource() > The RDFSource object couldn't be created.");
			}
			throw e;
		}

		return rdfSource;
	}

}
