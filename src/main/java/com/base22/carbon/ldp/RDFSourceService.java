package com.base22.carbon.ldp;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.RDFResourceClass;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.RDFSourceClass;
import com.base22.carbon.ldp.models.RDFSourceFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

@Service("s_RDFSourceService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RDFSourceService extends AbstractLDPService {

	public boolean rdfSourcesExist(List<String> uris) throws CarbonException {
		return uriObjectDAO.uriObjectsExist(uris);
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

	@PreAuthorize("hasPermission(#documentURIObject, 'UPDATE')")
	public DateTime touchRDFSource(URIObject sourceURIObject, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		DateTime modified = touchRDFSource(sourceURIObject, template);
		template.execute();
		return modified;
	}

	protected DateTime touchRDFSource(URIObject sourceURIObject, WriteTransactionTemplate template) throws CarbonException {
		final DateTime modified = DateTime.now();

		modelService.deleteProperty(sourceURIObject, sourceURIObject.getURI(), RDFSourceClass.Properties.MODIFIED.getProperty(), template);

		Resource resource = ResourceFactory.createResource(sourceURIObject.getURI());
		RDFNode object = ResourceFactory.createTypedLiteral(modified.toString(), XSDDatatype.XSDdateTime);
		Statement modifiedStatement = ResourceFactory.createStatement(resource, RDFSourceClass.Properties.MODIFIED.getProperty(), object);
		modelService.addStatements(sourceURIObject, Arrays.asList(modifiedStatement), template);

		return modified;
	}

	protected void touchRDFSource(URIObject sourceURIObject, DateTime modified, WriteTransactionTemplate template) throws CarbonException {

		modelService.deleteProperty(sourceURIObject, sourceURIObject.getURI(), RDFSourceClass.Properties.MODIFIED.getProperty(), template);

		Resource resource = ResourceFactory.createResource(sourceURIObject.getURI());
		RDFNode object = ResourceFactory.createTypedLiteral(modified.toString(), XSDDatatype.XSDdateTime);
		Statement modifiedStatement = ResourceFactory.createStatement(resource, RDFSourceClass.Properties.MODIFIED.getProperty(), object);
		modelService.addStatements(sourceURIObject, Arrays.asList(modifiedStatement), template);
	}

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_ACCESS_POINT')")
	public URIObject createAccessPoint(Container accessPointContainer, URIObject parentURIObject, String dataset) throws CarbonException {
		URIObject uriObject = createChildRDFSource(accessPointContainer, parentURIObject, dataset);
		try {
			addAccessPoint(accessPointContainer, parentURIObject, dataset);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
		return uriObject;
	}

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_ACCESS_POINT')")
	public DateTime createAccessPoints(List<Container> accessPoints, URIObject parentURIObject, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);

		DateTime timestamp = DateTime.now();

		for (Container accessPoint : accessPoints) {
			accessPoint.setTimestamps(timestamp, timestamp);
		}

		List<URIObject> uriObjects = createChildRDFSources(accessPoints, parentURIObject, template);

		for (Container accessPoint : accessPoints) {
			addAccessPoint(accessPoint, parentURIObject, template);
		}

		touchRDFSource(parentURIObject, timestamp, template);

		template.execute();

		return timestamp;
	}

	protected void addAccessPoint(Container accessPointContainer, URIObject parentURIObject, String dataset) throws CarbonException {
		String resourceURI = parentURIObject.getURI();

		StringBuffer accessPointURIBuilder = new StringBuffer();
		//@formatter:off
		accessPointURIBuilder
			.append(resourceURI)
			.append(RDFSourceClass.ACCESS_POINT_PREFIX)
			.append(accessPointContainer.getSlug())
		;
		//@formatter:on
		String accessPointURI = accessPointURIBuilder.toString();

		StringBuffer query = new StringBuffer();

		//@formatter:off
		query
			.append("INSERT DATA {")
				.append("\n\tGRAPH <")
					.append(resourceURI)
				.append("> {")
					.append("\n\t\t<")
						.append(resourceURI)
					.append("> <")
						.append(RDFSourceClass.HAS_ACCESS_POINT)
					.append("> <")
						.append(accessPointURI)
					.append(">.")
					
					.append("\n\t\t<")
						.append(accessPointURI)
					.append(">")
						.append("\n\t\t\t<")
							.append(RDFResourceClass.Properties.RDF_TYPE.getUri())
						.append("> <")
							.append(RDFSourceClass.ACCESS_POINT_CLASS)
						.append(">;")
						.append("\n\t\t\t<")
							.append(RDFSourceClass.CONTAINER)
						.append("> <")
							.append(accessPointContainer.getURI())
						.append(">;")
						.append("\n\t\t\t<")
							.append(RDFSourceClass.FOR_PROPERTY)
						.append("> <")
							.append(accessPointContainer.getMembershipTriplesPredicate())
						.append(">.")
				.append("\n\t}")
			.append("}")
		;
		//@formatter:on

		sparqlService.update(query.toString(), dataset);
	}

	protected void addAccessPoint(Container accessPoint, URIObject parentURIObject, WriteTransactionTemplate template) throws CarbonException {
		String resourceURI = parentURIObject.getURI();

		StringBuffer accessPointURIBuilder = new StringBuffer();
		//@formatter:off
		accessPointURIBuilder
			.append(resourceURI)
			.append(RDFSourceClass.ACCESS_POINT_PREFIX)
			.append(accessPoint.getSlug())
		;
		//@formatter:on
		String accessPointURI = accessPointURIBuilder.toString();

		StringBuffer query = new StringBuffer();

		//@formatter:off
		query
			.append("INSERT DATA {")
				.append("\n\tGRAPH <")
					.append(resourceURI)
				.append("> {")
					.append("\n\t\t<")
						.append(resourceURI)
					.append("> <")
						.append(RDFSourceClass.HAS_ACCESS_POINT)
					.append("> <")
						.append(accessPointURI)
					.append(">.")
					
					.append("\n\t\t<")
						.append(accessPointURI)
					.append(">")
						.append("\n\t\t\t<")
							.append(RDFResourceClass.Properties.RDF_TYPE.getUri())
						.append("> <")
							.append(RDFSourceClass.ACCESS_POINT_CLASS)
						.append(">;")
						.append("\n\t\t\t<")
							.append(RDFSourceClass.CONTAINER)
						.append("> <")
							.append(accessPoint.getURI())
						.append(">;")
						.append("\n\t\t\t<")
							.append(RDFSourceClass.FOR_PROPERTY)
						.append("> <")
							.append(accessPoint.getMembershipTriplesPredicate())
						.append(">.")
				.append("\n\t}")
			.append("}")
		;
		//@formatter:on

		sparqlService.update(query.toString(), template);
	}

	// TODO: Delete ACLs and URIObjects in the same transaction

	@PreAuthorize("hasPermission(#documentURIObject, 'DELETE')")
	public void deleteRDFSource(URIObject sourceURIObject, boolean deleteOcurrences, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);

		String sourceURI = sourceURIObject.getURI();

		String childrenBaseURI = sourceURI.endsWith("/") ? sourceURI : sourceURI.concat("/");

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("DELETE {")
				.append("\n\tGRAPH <")
					.append(sourceURI)
				.append("> {")
					.append("\n\t\t?subject ?predicate ?object")
				.append("\n\t}.")
				.append("\n\tGRAPH ?childGraphs {")
					.append("\n\t\t?childSubject ?childPredicate ?childObject")
				.append("\n\t}.")
				.append("\n\tGRAPH ?childSubject {")
					.append("\n\t\t?childSecondarySubject ?childSecondaryPredicate ?childSecondaryObject")
				.append("\n\t}.")
			.append("\n} WHERE {")
				.append("\n\tGRAPH <")
					.append(sourceURI)
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

		sparqlService.update(query.toString(), template);

		if ( deleteOcurrences ) {
			try {
				deleteRDFSourceOcurrences(sourceURIObject, template);
			} catch (CarbonException e) {
				// TODO: FT
				throw e;
			}
		}

		template.execute();

		try {
			uriObjectDAO.deleteURIObject(sourceURIObject, true);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		try {
			permissionService.deleteACL(sourceURIObject, true);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
	}

	// TODO: Update the modified of everything affected

	protected void deleteRDFSourceOcurrences(URIObject sourceURIObject, WriteTransactionTemplate template) throws CarbonException {
		String sourceURI = sourceURIObject.getURI();

		String childrenBaseURI = sourceURI.endsWith("/") ? sourceURI : sourceURI.concat("/");

		// TODO: Separate the childrenURI from the documentURI

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("DELETE {")
				.append("\n\tGRAPH ?removeFromGraphs {")
					.append("\n\t\t?subjectToRemove ?predicateToRemove ?subject")
				.append("\n\t}")
			.append("\n} WHERE {")
				.append("\n\tGRAPH ?removeFromGraphs {")
					.append("\n\t\t?subjectToRemove ?predicateToRemove ?subject")
					.append("\n\t\tFILTER( isIRI(?subject) && (?subject = <")
						.append(sourceURI)
					.append("> || regex(str(?subject), \"^")
						.append(childrenBaseURI)
					.append("\")) ).")
				.append("\n\t}")
			.append("\n}")
		;
		//@formatter:on

		sparqlService.update(query.toString(), template);
	}

}
