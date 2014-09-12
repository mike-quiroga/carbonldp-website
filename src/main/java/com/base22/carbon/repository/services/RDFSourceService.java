package com.base22.carbon.repository.services;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.AbstractLDPService;
import com.base22.carbon.ldp.ModelUtil;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.RDFSourceFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

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

	@PreAuthorize("hasPermission(#sourceURIObject, 'READ')")
	public RDFSource getRDFSource(URIObject sourceURIObject, String dataset) throws CarbonException {
		String documentURI = sourceURIObject.getURI();

		Model model = modelService.getNamedModel(documentURI, dataset);
		model = ModelUtil.createDetachedCopy(model);

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

	@PreAuthorize("hasPermission(#sourceURIObject, 'READ')")
	public RDFSource getRDFSourceBranch(URIObject sourceURIObject, String dataset) throws CarbonException {
		RDFSource source = null;

		String documentURI = sourceURIObject.getURI();

		String childrenBaseURI = documentURI.endsWith("/") ? documentURI : documentURI.concat("/");

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("CONSTRUCT {")
					.append("\n\t?subject ?predicate ?object.")
					.append("\n\t?childSubject ?childPredicate ?childObject.")
					.append("\n\t?childSecondarySubject ?childSecondaryPredicate ?childSecondaryObject")
			.append("\n} WHERE {")
				.append("\n\tGRAPH <")
					.append(documentURI)
				.append("> {")
					.append("\n\t\t?subject ?predicate ?object")
				.append("\n\t}.")
				.append("\n\tOPTIONAL {")
					.append("\n\t\tGRAPH ?childGraphs {")
						.append("\n\t\t\t?childSubject ?childPredicate ?childObject")
						.append("\n\t\t\tFILTER( STRSTARTS(str(?childSubject), \"")
							.append(childrenBaseURI)
						.append("\") )")
					.append("\n\t\t}")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\tGRAPH ?childSubject {")
							.append("\n\t\t\t\t?childSecondarySubject ?childSecondaryPredicate ?childSecondaryObject")
						.append("\n\t\t\t}.")
					.append("\n\t\t}.")
				.append("\n\t}.")
			.append("\n}")
		;
		//@formatter:on

		Model model = sparqlService.construct(query.toString(), dataset);

		RDFSourceFactory factory = new RDFSourceFactory();
		try {
			source = factory.create(documentURI, model);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getRDFSourceBranch() > The RDFSource object couldn't be created.");
			}
			throw e;
		}

		return source;
	}

	// === SPARQL Methods

	@PreAuthorize("hasPermission(#sourceURIObject, 'EXECUTE_SPARQL_QUERY')")
	public ResultSet executeSELECT(URIObject sourceURIObject, Query query, String dataset) throws CarbonException {
		RDFSource rdfSourceBranch = null;
		try {
			rdfSourceBranch = this.getRDFSourceBranch(sourceURIObject, dataset);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeSELECT() > Couldn't get the branch of the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}

		ResultSet resultSet = null;
		try {
			resultSet = sparqlService.select(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeSELECT() > The SELECT query couldn't be executed in the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}
		return resultSet;
	}

	@PreAuthorize("hasPermission(#sourceURIObject, 'EXECUTE_SPARQL_QUERY')")
	public Model executeCONSTRUCT(URIObject sourceURIObject, Query query, String dataset) throws CarbonException {
		RDFSource rdfSourceBranch = null;
		try {
			rdfSourceBranch = this.getRDFSourceBranch(sourceURIObject, dataset);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeCONSTRUCT() > Couldn't get the branch of the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}

		Model model = null;
		try {
			model = sparqlService.construct(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeCONSTRUCT() > The CONSTRUCT query couldn't be executed in the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}
		return model;
	}

	@PreAuthorize("hasPermission(#sourceURIObject, 'EXECUTE_SPARQL_QUERY')")
	public Model executeDESCRIBE(URIObject sourceURIObject, Query query, String dataset) throws CarbonException {
		RDFSource rdfSourceBranch = null;
		try {
			rdfSourceBranch = this.getRDFSourceBranch(sourceURIObject, dataset);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeDESCRIBE() > Couldn't get the branch of the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}

		Model model = null;
		try {
			model = sparqlService.describe(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeDESCRIBE() > The DESCRIBE query couldn't be executed in the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}
		return model;
	}

	@PreAuthorize("hasPermission(#sourceURIObject, 'EXECUTE_SPARQL_QUERY')")
	public Boolean executeASK(URIObject sourceURIObject, Query query, String dataset) throws CarbonException {
		RDFSource rdfSourceBranch = null;
		try {
			rdfSourceBranch = this.getRDFSourceBranch(sourceURIObject, dataset);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeASK() > Couldn't get the branch of the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}

		Boolean result = null;
		try {
			result = sparqlService.ask(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeASK() > The ASK query couldn't be executed in the RDFSource: '{}'.", sourceURIObject.getURI());
			}
			throw e;
		}
		return result;
	}

	// === End: SPARQL Methods

}
