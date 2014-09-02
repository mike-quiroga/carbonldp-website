package com.base22.carbon.repository;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.CarbonException;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.ldp.LDPC;
import com.base22.carbon.ldp.LDPC.ContainerType;
import com.base22.carbon.ldp.LDPContainer;
import com.base22.carbon.ldp.LDPContainerFactory;
import com.base22.carbon.ldp.LDPContainerQueryOptions;
import com.base22.carbon.ldp.LDPContainerQueryOptions.METHOD;
import com.base22.carbon.ldp.LDPNR;
import com.base22.carbon.ldp.LDPR;
import com.base22.carbon.ldp.LDPRS;
import com.base22.carbon.ldp.LDPRSource;
import com.base22.carbon.ldp.LDPRSourceFactory;
import com.base22.carbon.ldp.URIObject;
import com.base22.carbon.ldp.URIObjectDAO;
import com.base22.carbon.ldp.WrapperForLDPNR;
import com.base22.carbon.ldp.WrapperForLDPNRFactory;
import com.base22.carbon.sparql.SparqlQuery;
import com.base22.carbon.sparql.SparqlService;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

@Service("ldpService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class LDPService {

	@Autowired
	private RdfService rdfService;
	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private URIObjectDAO uriObjectDAO;
	@Autowired
	private PermissionService permissionService;

	static final Logger LOG = LoggerFactory.getLogger(LDPService.class);

	public void init() {
	}

	// ========= LDP-RS Related Methods

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPRS')")
	public URIObject createChildLDPRSource(LDPRSource ldpRSource, URIObject parentURIObject, String dataset) throws CarbonException {
		String documentURI = ldpRSource.getURI();

		// Create the URIObject for the LDPRSource
		URIObject documentURIObject = new URIObject(documentURI);
		try {
			documentURIObject = uriObjectDAO.createURIObject(documentURIObject);
		} catch (CarbonException e) {
			throw e;
		}

		// Add the namedModel to the dataset
		Model sourceModel = ldpRSource.getResource().getModel();
		try {
			rdfService.addNamedModel(documentURI, sourceModel, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		try {
			permissionService.setParent(documentURIObject, parentURIObject);
		} catch (CarbonException e) {
			throw e;
		}

		return documentURIObject;
	}

	// TODO: Refactor Method
	// TODO: Take into account secured properties
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public LDPRSource getLDPRSource(URIObject documentURIObject, String dataset) throws CarbonException {
		LDPRSource ldpRSource = null;

		String documentURI = documentURIObject.getURI();

		Model model = rdfService.getNamedModel(documentURI, dataset);
		model = model.difference(ModelFactory.createDefaultModel());

		LDPRSourceFactory factory = new LDPRSourceFactory();
		try {
			ldpRSource = factory.create(documentURI, model);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getLDPRSource() > The LDPRSource object couldn't be created.");
			}
			throw e;
		}

		return ldpRSource;
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public String getETagofLDPRSource(URIObject documentURIObject, String dataset) throws CarbonException {
		String eTag = null;

		LOG.trace(">> getETagofLDPRSource()");

		String documentURI = documentURIObject.getURI();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("SELECT ?etag WHERE { ")
				.append("GRAPH <")
					.append(documentURI)
				.append("> { ")
					.append("<")
						.append(documentURI)
						.append("> <")
							.append(LDPRS.MODIFIED)
						.append("> ?etag.")
				.append("}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		ResultSet resultSet = sparqlService.select(sparqlQuery);
		if ( resultSet.hasNext() ) {
			QuerySolution solution = resultSet.next();
			eTag = solution.get("etag").toString();
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- getETagofLDPRSource() > Got: {}", eTag);
			}
		}

		sparqlService.closeResultSet(resultSet);

		return eTag;
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public Set<String> getDocumentTypes(URIObject documentURIObject, String dataset) throws CarbonException {
		Set<String> documentTypes = new HashSet<String>();

		LOG.trace(">> getDocumentTypes()");

		String documentURI = documentURIObject.getURI();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("SELECT ?type WHERE { ")
				.append("GRAPH <")
					.append(documentURI)
				.append("> { ")
					.append("<")
						.append(documentURI)
					.append("> <")
						.append(LDPR.Properties.RDF_TYPE.getUri())
					.append("> ?type.")
				.append("}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		ResultSet resultSet = sparqlService.select(sparqlQuery);
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			String docType = solution.get("type").toString();
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("--  getDocumentTypes() > Got: {}", docType);
			}
			documentTypes.add(docType);
		}

		sparqlService.closeResultSet(resultSet);

		return documentTypes;
	}

	public String getRealURIofLDPRSource(String documentURI, String dataset) throws CarbonException {
		String realURI = null;

		if ( rdfService.namedModelExists(documentURI, dataset) ) {
			realURI = documentURI;
		}
		documentURI = documentURI.concat("/");
		if ( rdfService.namedModelExists(documentURI, dataset) ) {
			realURI = documentURI;
		}

		return realURI;
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'UPDATE')")
	public long touchLDPRSource(URIObject documentURIObject, String dataset) throws CarbonException {
		String documentURI = documentURIObject.getURI();

		DateTime now = DateTime.now();

		// Create remove older modified timestamp sparql query
		rdfService.deleteTriples(documentURI, documentURI, LDPRS.MODIFIED, null, dataset);

		// Insert the new timestamp
		rdfService.insertTriple(documentURI, documentURI, LDPRS.MODIFIED, now.getMillis(), dataset);
		return now.getMillis();
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'EXTEND')")
	public void replaceLDPRSource(LDPRSource ldpRSource, URIObject documentURIObject, String dataset) throws CarbonException {
		try {
			deleteLDPRSource(documentURIObject, false, dataset);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
		// Add the namedModel to the dataset
		Model sourceModel = ldpRSource.getResource().getModel();
		try {
			rdfService.addNamedModel(documentURIObject.getURI(), sourceModel, dataset);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'DELETE')")
	public void deleteLDPRSource(URIObject documentURIObject, boolean deleteOcurrences, String dataset) throws CarbonException {
		String documentURI = documentURIObject.getURI();

		String childrenBaseURI = documentURI.endsWith("/") ? documentURI : documentURI.concat("/");

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("DELETE {")
				.append("\n\tGRAPH <")
					.append(documentURI)
				.append("> {")
					.append("\n\t\t?subject ?predicate ?object")
				.append("\n\t}.")
				.append("\n\tGRAPH ?childGraphs {")
					.append("\n\t\t?childSubject ?childPredicate ?childObject")
				.append("\n\t}.")
				.append("\n\tGRAPH ?childSubject {")
					.append("\n\t\t?childSecondarySubject ?childSecondaryPredicate ?childSecondaryObject")
				.append("\n\t}.")
			.append("} WHERE {")
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
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);

		if ( deleteOcurrences ) {
			try {
				deleteLDPRSourceOcurrences(documentURIObject, dataset);
			} catch (CarbonException e) {
				// TODO: FT
				throw e;
			}
			try {
				permissionService.deleteACL(documentURIObject, true);
			} catch (CarbonException e) {
				// TODO: FT
				throw e;
			}
			try {
				uriObjectDAO.deleteURIObject(documentURIObject, true);
			} catch (CarbonException e) {
				// TODO: FT
				throw e;
			}
		}

	}

	// TODO: Decide. Should we make this private?
	public void deleteLDPRSourceOcurrences(URIObject documentURIObject, String dataset) throws CarbonException {
		String documentURI = documentURIObject.getURI();

		String childrenBaseURI = documentURI.endsWith("/") ? documentURI : documentURI.concat("/");

		// TODO: Separate the childrenURI from the documentURI

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

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
						.append(documentURI)
					.append("> || regex(str(?subject), \"^")
						.append(childrenBaseURI)
					.append("\")) ).")
				.append("\n\t}")
			.append("\n}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);
	}

	// ========= End: LDP-RS Related Methods
	// ========= LDP-NR Related Methods

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_WFLDPNR')")
	public URIObject createChildLDPNR(WrapperForLDPNR wrapper, URIObject parentURIObject, String dataset) throws CarbonException {
		String documentURI = wrapper.getURI();

		// Create the URIObject for the LDPRSource
		URIObject documentURIObject = new URIObject(documentURI);
		try {
			documentURIObject = uriObjectDAO.createURIObject(documentURIObject);
		} catch (CarbonException e) {
			throw e;
		}

		// Add the namedModel to the dataset
		Model sourceModel = wrapper.getResource().getModel();
		try {
			rdfService.addNamedModel(documentURI, sourceModel, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		try {
			permissionService.setParent(documentURIObject, parentURIObject);
		} catch (CarbonException e) {
			throw e;
		}

		return documentURIObject;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public WrapperForLDPNR getWrapperForLDPNR(URIObject documentURIObject, String dataset) throws CarbonException {
		WrapperForLDPNR wrapper = null;

		String documentURI = documentURIObject.getURI();

		Model model = null;
		try {
			model = rdfService.getNamedModel(documentURI, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		WrapperForLDPNRFactory factory = new WrapperForLDPNRFactory();
		try {
			wrapper = factory.create(documentURI, model);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getWrapperForLDPNR() > The LDPRSource object couldn't be created.");
			}
			throw e;
		}

		return wrapper;
	}

	// TODO: Move this to a Util Class
	public boolean documentIsWrapperForLDPNR(Set<String> documentTypes) {
		boolean itIs = false;

		if ( documentTypes.contains(LDPNR.TYPE) ) {
			itIs = true;
		}

		return itIs;
	}

	// ========= End: LDP-NR Related Methods
	// ========= LDP-C Related Methods

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	public URIObject createRootContainer(LDPContainer container, String dataset) throws CarbonException {
		String documentURI = container.getURI();

		// Create the URIObject for the LDPRSource
		URIObject documentURIObject = new URIObject(documentURI);
		try {
			documentURIObject = uriObjectDAO.createURIObject(documentURIObject);
		} catch (CarbonException e) {
			throw e;
		}

		// Add the namedModel to the dataset
		Model sourceModel = container.getResource().getModel();
		try {
			rdfService.addNamedModel(documentURI, sourceModel, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		return documentURIObject;
	}

	// TODO: Implement
	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPC')")
	public URIObject createChildLDPContainer(LDPContainer container, URIObject parentURIObject, String dataset) throws CarbonException {
		return createChildLDPRSource(container, parentURIObject, dataset);
	}

	// TODO: Move this to a Util Class
	public boolean documentIsContainer(Set<String> documentTypes) {
		boolean itIs = false;

		if ( documentTypes.contains(LDPC.BASIC) )
			itIs = true;
		if ( documentTypes.contains(LDPC.DIRECT) )
			itIs = true;
		if ( documentTypes.contains(LDPC.INDIRECT) )
			itIs = true;

		return itIs;
	}

	// TODO: Refactor
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public boolean documentIsContainer(URIObject documentURIObject, String dataset) throws CarbonException {
		boolean itIs = false;

		String documentURI = documentURIObject.getURI();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("ASK { ")
				.append("GRAPH <")
					.append(documentURI)
				.append("> { ")
					.append("<")
						.append(documentURI)
					.append("> <")
						.append(LDPR.Properties.RDF_TYPE.getUri())
					.append("> ?type.")
					.append("FILTER(")
						.append("?type = <")
							.append(LDPC.BASIC)
						.append(">")
						.append("||")
						.append("?type = <")
							.append(LDPC.DIRECT)
						.append(">")
						.append("||")
						.append("?type = <")
							.append(LDPC.INDIRECT)
						.append(">")
					.append(")")
				.append("}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		itIs = sparqlService.ask(sparqlQuery);

		return itIs;
	}

	// TODO: Move this to a Util Class
	public String getDocumentContainerType(Set<String> documentTypes) {

		for (String docType : documentTypes) {
			if ( docType.equalsIgnoreCase(LDPC.BASIC) ) {
				return LDPC.BASIC;
			} else if ( docType.equalsIgnoreCase(LDPC.DIRECT) ) {
				return LDPC.DIRECT;
			} else if ( docType.equalsIgnoreCase(LDPC.INDIRECT) ) {
				return LDPC.INDIRECT;
			}
		}

		return null;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public ContainerType getDocumentContainerType(URIObject documentURIObject, String dataset) throws CarbonException {
		ContainerType containerType = null;

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> getDocumentContainerType()");
		}

		String documentURI = documentURIObject.getURI();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("SELECT ?type WHERE { ")
				.append("GRAPH <")
					.append(documentURI)
				.append("> { ")
					.append("<")
						.append(documentURI)
					.append("> <")
						.append(LDPR.Properties.RDF_TYPE.getUri())
					.append("> ?type.")
					.append("FILTER(")
		;

		// Construct ContainerType filter
		ContainerType[] containerTypes = ContainerType.values();
		for (int i = 0; i < containerTypes.length; i++) {
			query
						.append("?type = ")
						.append(containerTypes[i].getPrefixedURI().getResourceURI())
			;
			if((i+1) != containerTypes.length) {
				// Not the last one
				query
						.append(" || ")
				;
			}
		}
		
		query
					.append(")")
				.append("}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		String containerTypeURI = null;
		ResultSet resultSet = sparqlService.select(sparqlQuery);
		if ( resultSet.hasNext() ) {
			QuerySolution solution = resultSet.next();
			containerTypeURI = solution.get("type").toString();
		}
		sparqlService.closeResultSet(resultSet);

		if ( containerTypeURI != null ) {
			containerType = ContainerType.findByURI(containerTypeURI);
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getDocumentContainerType() < The document isn't a container.");
			}
		}

		return containerType;
	}

	public InteractionModel getDefaultInteractionModel(URIObject documentURIObject, String dataset) throws CarbonException {
		InteractionModel interactionModel = null;

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> getDefaultInteractionModel()");
		}

		String documentURI = documentURIObject.getURI();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("SELECT ?model WHERE { ")
				.append("GRAPH <")
					.append(documentURI)
				.append("> { ")
					.append("<")
						.append(documentURI)
					.append("> <")
						.append(LDPC.DIM)
					.append("> ?model.")
				.append("}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		String interactionModelURI = null;
		ResultSet resultSet = sparqlService.select(sparqlQuery);
		while (resultSet.hasNext() && interactionModelURI == null) {
			QuerySolution solution = resultSet.next();
			Node node = solution.get("model").asNode();
			if ( node.isURI() ) {
				interactionModelURI = node.getURI();
			}
		}
		sparqlService.closeResultSet(resultSet);

		if ( interactionModelURI != null ) {
			interactionModel = InteractionModel.findByURI(interactionModelURI);
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getDefaultInteractionModel() < The document doesn't have a default interaction model.");
			}
		}

		return interactionModel;

	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public LDPContainer getLDPContainer(URIObject documentURIObject, String dataset) throws CarbonException {
		return getLDPContainer(documentURIObject, dataset, new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.GET));
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public LDPContainer getLDPContainer(URIObject documentURIObject, String dataset, String containerType) throws CarbonException {
		return getLDPContainer(documentURIObject, dataset, containerType, new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.GET));
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public LDPContainer getLDPContainer(URIObject documentURIObject, String dataset, LDPContainerQueryOptions options) throws CarbonException {
		ContainerType containerType = null;

		try {
			containerType = getDocumentContainerType(documentURIObject, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		if ( containerType == null ) {
			// TODO: Finish this
			throw new CarbonException("");
		}

		return getLDPContainer(documentURIObject, dataset, containerType.getURI(), new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.GET));
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public LDPContainer getLDPContainer(URIObject documentURIObject, String dataset, String containerType, LDPContainerQueryOptions options)
			throws CarbonException {
		LDPContainer ldpContainer = null;

		String documentURI = documentURIObject.getURI();

		String query = null;
		if ( containerType.equals(LDPC.BASIC) ) {
			query = prepareLDPBasicContainerQuery(documentURI, options);
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			query = prepareLDPDirectContainerQuery(documentURI, options);
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			query = prepareLDPIndirectContainerQuery(documentURI, options);
		} else {
			// TODO: Finish this
			throw new CarbonException("");
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("-- getLDPContainer() > container type: {}", containerType);
		}

		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.QUERY, dataset, query);

		Model ldpContainerModel;
		ldpContainerModel = sparqlService.construct(sparqlQuery);

		LDPContainerFactory factory = new LDPContainerFactory();
		try {
			ldpContainer = factory.create(documentURI, ldpContainerModel);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getLDPContainer() > The LDPContainer object couldn't be created.");
			}
			throw e;
		}

		return ldpContainer;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public int[] countLDPContainer(URIObject documentURIObject, String dataset, String containerType) throws CarbonException {
		int contained = 0;
		int members = 0;

		String documentURI = documentURIObject.getURI();
		StringBuilder queryBuilder = new StringBuilder();

		if ( containerType.equals(LDPC.BASIC) ) {
			//@formatter:off
			queryBuilder
				.append("SELECT (COUNT(distinct ?containedObjects) as ?contained) (COUNT(distinct ?memberObjects) as ?members)")
				.append("\nWHERE {")
					.append("\n\tOPTIONAL {")
						.append("\n\t\tGRAPH <")
							.append(documentURI)
						.append("> {")
							.append("\n\t\t\t<")
								.append(documentURI)
							.append("> <")
							.append(LDPC.CONTAINS)
							.append("> ?containedObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
					.append("\n\tOPTIONAL {")
						.append("\n\t\tGRAPH <")
							.append(documentURI)
						.append("> {")
							.append("\n\t\t\t<")
								.append(documentURI)
							.append("> <")
							.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
							.append("> ?memberObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			//@formatter:off
			queryBuilder
				.append("SELECT (COUNT(distinct ?containedObjects) as ?contained) (COUNT(distinct ?memberObjects) as ?members)")
				.append("\nWHERE {")
					.append("\n\tOPTIONAL {")
						.append("\n\t\tGRAPH <")
							.append(documentURI)
						.append("> {")
							.append("\n\t\t\t<")
								.append(documentURI)
							.append("> <")
							.append(LDPC.CONTAINS)
							.append("> ?containedObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
					.append("\n\tGRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t<")
							.append(documentURI)
						.append("> ")
						.append("\n\t\t\t<")
						.append(LDPC.MEMBERSHIP_RESOURCE)
						.append("> ?membershipResource;")
						.append("\n\t\t\t<")
						.append(LDPC.HAS_MEMBER_RELATION)
						.append("> ?hasMemberRelation.")
					.append("\n\t}")
					.append("\n\tOPTIONAL {")
						.append("\n\t\tGRAPH ?membershipResource {")
							.append("\n\t\t\t?membershipResource ?hasMemberRelation ?memberObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			//@formatter:off
			queryBuilder
				.append("SELECT (COUNT(distinct ?containedObjects) as ?contained) (COUNT(distinct ?memberObjects) as ?members)")
				.append("\nWHERE {")
					.append("\n\tOPTIONAL {")
						.append("\n\t\tGRAPH <")
							.append(documentURI)
						.append("> {")
							.append("\n\t\t\t<")
								.append(documentURI)
							.append("> <")
							.append(LDPC.CONTAINS)
							.append("> ?containedObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
					.append("\n\tGRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t<")
							.append(documentURI)
						.append("> ")
						.append("\n\t\t\t<")
						.append(LDPC.MEMBERSHIP_RESOURCE)
						.append("> ?membershipResource;")
						.append("\n\t\t\t<")
						.append(LDPC.HAS_MEMBER_RELATION)
						.append("> ?hasMemberRelation.")
					.append("\n\t}")
					.append("\n\tOPTIONAL {")
						.append("\n\t\tGRAPH ?membershipResource {")
							.append("\n\t\t\t?membershipResource ?hasMemberRelation ?memberObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
				.append("\n}")
			;
			//@formatter:on
		} else {
			// TODO: FT
			throw new CarbonException("");
		}

		String query = queryBuilder.toString();
		SparqlQuery sparqlQuery = new SparqlQuery(SparqlQuery.TYPE.QUERY, dataset, query);

		ResultSet resultSet = null;
		try {
			resultSet = sparqlService.select(sparqlQuery);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		if ( resultSet.hasNext() ) {
			QuerySolution solution = resultSet.next();
			if ( solution.contains("contained") ) {
				try {
					contained = solution.getLiteral("contained").getInt();
				} catch (Exception ignore) {
				}
			}
			if ( solution.contains("members") ) {
				try {
					members = solution.getLiteral("members").getInt();
				} catch (Exception ignore) {
				}
			}
		}

		return new int[] { contained, members };
	}

	//@formatter:off
	private String prepareLDPBasicContainerQuery(String documentURI, LDPContainerQueryOptions options) {
		StringBuffer query = new StringBuffer();
				
		if( options.includeContainerProperties() && !options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// TRUE && FALSE && FALSE
			// Just container properties
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
						.append("\n\t\tFILTER( ?containerPredicate != <")
						.append(LDPC.CONTAINS)
						.append("> && ?containerPredicate != <")
						.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
						.append("> )")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// TRUE && TRUE && FALSE
			// Container properties and containment triples
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
						.append("\n\t\tFILTER( ?containerPredicate != <")
						.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
						.append("> )")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && !options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// TRUE && FALSE && TRUE
			// Container properties and membership triples
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
						.append("\n\t\tFILTER( ?containerPredicate != <")
						.append(LDPC.CONTAINS)
						.append("> )")
					.append("\n\t}")
				.append("\n}")
			;	
		
		} else if( options.includeContainerProperties() && options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// TRUE && TRUE && TRUE
			// Container properties, containment triples and membership triples
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
					.append("\n\t\t}.")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && !options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// FALSE && FALSE && FALSE
			
			// Bad combination, you are asking for something empty
			return null;
		
		} else if( !options.includeContainerProperties() && options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// FALSE && TRUE && FALSE
			// Containment triples
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> <")
						.append(LDPC.CONTAINS)
					.append("> ?containedObjects.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append("> <")
							.append(LDPC.CONTAINS)
						.append("> ?containedObjects.")
					.append("\n\t\t}.")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && !options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// FALSE && FALSE && TRUE
			// Membership triples
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> <")
						.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
					.append("> ?membershipObjects.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append("> <")
							.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
						.append("> ?membershipObjects.")
					.append("\n\t\t}.")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// FALSE && TRUE && TRUE
			// Containment triples and Membership triples
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append(">")
						.append("\n\t\t<")
							.append(LDPC.CONTAINS)
						.append("> ?containedObjects;")
						.append("\n\t\t<")
							.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
						.append("> ?membershipObjects.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t<")
								.append(LDPC.CONTAINS)
							.append("> ?containedObjects;")
							.append("\n\t\t\t\t<")
								.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
							.append("> ?membershipObjects.")
					.append("\n\t\t}.")
				.append("\n}")
			;
		}
		
		return query.toString();
	}
	//@formatter:on

	//@formatter:off
	private String prepareLDPDirectContainerQuery(String documentURI, LDPContainerQueryOptions options) {
		StringBuffer query = new StringBuffer();
				
		if( options.includeContainerProperties() && !options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// TRUE && FALSE && FALSE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
						.append("\n\t\tFILTER( ?containerPredicate != <")
						.append(LDPC.CONTAINS)
						.append(">)")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// TRUE && TRUE && FALSE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && !options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// TRUE && FALSE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject.")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t?containerPredicate ?containerObject;")
							.append("\n\t\t\t\t<")
								.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
								.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
							.append("\n\t\tFILTER( ?containerPredicate != <")
							.append(LDPC.CONTAINS)
							.append(">)")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// TRUE && TRUE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject.")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t?containerPredicate ?containerObject;")
							.append("\n\t\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && !options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// FALSE && FALSE && FALSE
			
			// Bad combination, you are asking for something empty
			return null;
		
		} else if( !options.includeContainerProperties() && options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// FALSE && TRUE && FALSE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> <")
					.append(LDPC.CONTAINS)
					.append("> ?containedObjects ")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> <")
						.append(LDPC.CONTAINS)
						.append("> ?containedObjects ")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && !options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// FALSE && FALSE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// FALSE && TRUE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> <")
					.append(LDPC.CONTAINS)
					.append("> ?containedObjects ")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t<")
							.append(LDPC.CONTAINS)
							.append("> ?containedObjects")
							.append("\n\t\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		}
		
		return query.toString();
	}
	//@formatter:on

	//@formatter:off
	private String prepareLDPIndirectContainerQuery(String documentURI, LDPContainerQueryOptions options) {
		StringBuffer query = new StringBuffer();
				
		if( options.includeContainerProperties() && !options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// TRUE && FALSE && FALSE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
						.append("\n\t\tFILTER( ?containerPredicate != <")
						.append(LDPC.CONTAINS)
						.append(">)")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// TRUE && TRUE && FALSE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> ?containerPredicate ?containerObject.")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && !options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// TRUE && FALSE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject.")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t?containerPredicate ?containerObject;")
							.append("\n\t\t\t\t<")
								.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
								.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
							.append("\n\t\tFILTER( ?containerPredicate != <")
							.append(LDPC.CONTAINS)
							.append(">)")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		
		} else if( options.includeContainerProperties() && options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// TRUE && TRUE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> ?containerPredicate ?containerObject.")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t?containerPredicate ?containerObject;")
							.append("\n\t\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && !options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// FALSE && FALSE && FALSE
			
			// Bad combination, you are asking for something empty
			return null;
		
		} else if( !options.includeContainerProperties() && options.includeContainmentTriples() && !options.includeMembershipTriples() ) {
			// FALSE && TRUE && FALSE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> <")
					.append(LDPC.CONTAINS)
					.append("> ?containedObjects ")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> <")
						.append(LDPC.CONTAINS)
						.append("> ?containedObjects ")
					.append("\n\t}")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && !options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// FALSE && FALSE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		
		} else if( !options.includeContainerProperties() && options.includeContainmentTriples() && options.includeMembershipTriples() ) {
			// FALSE && TRUE && TRUE
			
			query
				.append("CONSTRUCT {")
					.append("\n\t<")
						.append(documentURI)
					.append("> <")
					.append(LDPC.CONTAINS)
					.append("> ?containedObjects ")
					.append("\n\t?membershipResource ?hasMemberRelation ?members.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t<")
							.append(LDPC.CONTAINS)
							.append("> ?containedObjects")
							.append("\n\t\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t\t}.")
					.append("\n\t\tOPTIONAL {")
						.append("\n\t\t\t GRAPH ?membershipResource {")
							.append("\n\t\t\t\t ?membershipResource ?hasMemberRelation ?members.")
						.append("\n\t\t\t}")
					.append("\n\t\t}")
				.append("\n}")
			;
		}
		
		return query.toString();
	}
	//@formatter:on

	@PreAuthorize("hasPermission(#containerURIObject, 'READ') and hasPermission(#memberURIObject, 'READ')")
	public boolean resourceIsMemberOfContainer(URIObject containerURIObject, URIObject memberURIObject, String dataset) throws CarbonException {
		ContainerType containerType = null;
		try {
			containerType = getDocumentContainerType(containerURIObject, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		if ( containerType == null ) {
			// TODO: Finish this
			throw new CarbonException("");
		}
		return resourceIsMemberOfContainer(containerURIObject, memberURIObject, dataset, containerType.getURI());
	}

	@PreAuthorize("hasPermission(#containerURIObject, 'READ') and hasPermission(#memberURIObject, 'DISCOVER')")
	public boolean resourceIsMemberOfContainer(URIObject containerURIObject, URIObject memberURIObject, String dataset, String containerType)
			throws CarbonException {
		boolean isMember = false;

		String containerURI = containerURIObject.getURI();
		String memberURI = memberURIObject.getURI();

		StringBuffer query = new StringBuffer();
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setDataset(dataset);
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);

		if ( containerType.equals(LDPC.BASIC) ) {
			//@formatter:off
			query
				.append("ASK {")
					.append("\n\tGRAPH <")
						.append(containerURI)
					.append("> {")
						.append("\n\t\t<")
							.append(containerURI)
						.append("> <")
						.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
						.append("> <")
							.append(memberURI)
						.append(">.")
					.append("\n\t}")
				.append("\n\t}")
			;
			//@formatter:on

			sparqlQuery.setQuery(query.toString());

			isMember = sparqlService.ask(sparqlQuery);
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			//@formatter:off
			query
				.append("SELECT ?membershipResource ?membershipProperty ?membershipObject WHERE {")
					.append("\n\tGRAPH <")
						.append(containerURI)
					.append("> {")
						.append("\n\t\t<")
							.append(containerURI)
						.append("> ")
							.append("\n\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?membershipRelation.")
					.append("\n\t}.")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?membershipProperty ?membershipObject")
						.append("\n\t\tFILTER(?membershipObject = <")
							.append(memberURI)
						.append(">).")
					.append("\n\t}.")
				.append("}")
			;
			//@formatter:on

			sparqlQuery.setQuery(query.toString());

			ResultSet resultSet = sparqlService.select(sparqlQuery);
			isMember = resultSet.getRowNumber() >= 0;

			sparqlService.closeResultSet(resultSet);
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			//@formatter:off
			query
				.append("SELECT ?membershipResource ?membershipProperty ?membershipObject WHERE {")
					.append("\n\tGRAPH <")
						.append(containerURI)
					.append("> {")
						.append("\n\t\t<")
							.append(containerURI)
						.append("> ")
							.append("\n\t\t\t<")
							.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
							.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?membershipRelation;")
							.append("\n\t\t\t<")
							.append(LDPC.ICR)
							.append("> ?icr.")
					.append("\n\t}.")
					.append("\n\tGRAPH <")
						.append(memberURI)
					.append("> {")
						.append("\n\t\t<")
							.append(memberURI)
						.append("> ?icr ?membershipObject.")
					.append("\n\t}.")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?membershipProperty ?membershipObject.")
					.append("\n\t}.")
				.append("}")
			;
			//@formatter:on

			sparqlQuery.setQuery(query.toString());

			ResultSet resultSet = sparqlService.select(sparqlQuery);
			isMember = resultSet.getRowNumber() >= 1;
		}

		return isMember;
	}

	// TODO: Decide. Should we make this private?
	public void addDocumentAsContainment(LDPContainer container, LDPRSource document, String dataset) throws CarbonException {
		String containerType = container.getTypeOfContainer();

		StringBuffer query = new StringBuffer();
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setDataset(dataset);
		sparqlQuery.setType(SparqlQuery.TYPE.UPDATE);

		if ( containerType.equals(LDPC.BASIC) ) {
			//@formatter:off
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(LDPC.CONTAINS)
								.append("> <")
								.append(document.getURI())
								.append(">;")
							.append("\n\t\t\t<")
								.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
								.append("> <")
								.append(document.getURI())
								.append(">.")
					.append("\n\t}")
				.append("}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			//@formatter:off
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(LDPC.CONTAINS)
								.append("> <")
								.append(document.getURI())
								.append(">.")
					.append("\n\t}.")
					.append("\n\tGRAPH <")
						.append(container.getMembershipResourceURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getMembershipResourceURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(container.getMembershipTriplesPredicate())
								.append("> <")
								.append(document.getURI())
								.append(">.")
					.append("\n\t}.")
				.append("}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			//@formatter:off
			// TODO: Handle different types of objects?
			Property icrPredicate = ResourceFactory.createProperty(container.getInsertedContentRelation());
			String membershipObject = document.getResource().getProperty(icrPredicate).getResource().getURI();
			
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(LDPC.CONTAINS)
								.append("> <")
								.append(document.getURI())
								.append(">.")
					.append("\n\t}.")
					.append("\n\tGRAPH <")
						.append(container.getMembershipResourceURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getMembershipResourceURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(container.getMembershipTriplesPredicate())
								.append("> <")
								.append(membershipObject)
								.append(">.")
					.append("\n\t}.")
				.append("}")
			;
			//@formatter:on
		}

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);

	}

	// TODO: Decide. How are we going to relate a URIObject with a LDPContainer object?
	public void addDocumentAsMember(LDPContainer container, LDPRSource document, String dataset) throws CarbonException {
		String containerType = container.getTypeOfContainer();

		StringBuffer query = new StringBuffer();
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setDataset(dataset);
		sparqlQuery.setType(SparqlQuery.TYPE.UPDATE);

		if ( containerType.equals(LDPC.BASIC) ) {
			//@formatter:off
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getURI())
						.append("> <")
							.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
							.append("> <")
							.append(document.getURI())
							.append(">.")
					.append("\n\t}")
				.append("}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			//@formatter:off
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getMembershipResourceURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getMembershipResourceURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(container.getMembershipTriplesPredicate())
								.append("> <")
								.append(document.getURI())
								.append(">.")
					.append("\n\t}.")
				.append("}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			//@formatter:off
			// TODO: Handle different types of objects?
			Property icrPredicate = ResourceFactory.createProperty(container.getInsertedContentRelation());
			Statement icrStatement = document.getResource().getProperty(icrPredicate);
			String membershipObject = icrStatement.getObject().asNode().getURI();
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getMembershipResourceURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getMembershipResourceURI())
						.append(">")
							.append("\n\t\t\t<")
								.append(container.getMembershipTriplesPredicate())
								.append("> <")
								.append(membershipObject)
								.append(">.")
					.append("\n\t}.")
				.append("}")
			;
			//@formatter:on
		}

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);
	}

	public void addInverseMembershipTriple(LDPContainer container, String memberURI, String dataset) throws CarbonException {

		StringBuffer query = new StringBuffer();
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setDataset(dataset);
		sparqlQuery.setType(SparqlQuery.TYPE.UPDATE);

		//@formatter:off
		query
			.append("INSERT DATA {")
				.append("\n\tGRAPH <")
					.append(memberURI)
				.append("> {")
					.append("\n\t\t<")
						.append(memberURI)
					.append("> <")
						.append(container.getMemberOfRelation())
						.append("> <")
						.append(container.getURI())
						.append(">.")
				.append("\n\t}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);
	}

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_ACCESS_POINT')")
	public URIObject createAccessPoint(LDPContainer accessPointContainer, URIObject parentURIObject, String dataset) throws CarbonException {
		URIObject uriObject = createChildLDPRSource(accessPointContainer, parentURIObject, dataset);
		try {
			addAccessPoint(accessPointContainer, parentURIObject, dataset);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
		return uriObject;
	}

	// TODO: Decide. Should we make this private?
	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_ACCESS_POINT')")
	public void addAccessPoint(LDPContainer accessPointContainer, URIObject parentURIObject, String dataset) throws CarbonException {
		String resourceURI = parentURIObject.getURI();

		StringBuffer accessPointURIBuilder = new StringBuffer();
		//@formatter:off
		accessPointURIBuilder
			.append(resourceURI)
			.append(LDPRS.ACCESS_POINT_PREFIX)
			.append(accessPointContainer.getSlug())
		;
		//@formatter:on
		String accessPointURI = accessPointURIBuilder.toString();

		StringBuffer query = new StringBuffer();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setDataset(dataset);
		sparqlQuery.setType(SparqlQuery.TYPE.UPDATE);

		//@formatter:off
		query
			.append("INSERT DATA {")
				.append("\n\tGRAPH <")
					.append(resourceURI)
				.append("> {")
					.append("\n\t\t<")
						.append(resourceURI)
					.append("> <")
						.append(LDPRS.HAS_ACCESS_POINT)
					.append("> <")
						.append(accessPointURI)
					.append(">.")
					
					.append("\n\t\t<")
						.append(accessPointURI)
					.append(">")
						.append("\n\t\t\t<")
							.append(LDPR.Properties.RDF_TYPE.getUri())
						.append("> <")
							.append(LDPRS.ACCESS_POINT_CLASS)
						.append(">;")
						.append("\n\t\t\t<")
							.append(LDPRS.CONTAINER)
						.append("> <")
							.append(accessPointContainer.getURI())
						.append(">;")
						.append("\n\t\t\t<")
							.append(LDPRS.FOR_PROPERTY)
						.append("> <")
							.append(accessPointContainer.getMembershipTriplesPredicate())
						.append(">.")
				.append("\n\t}")
			.append("}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);
	}

	private void deleteAccessPoint(LDPContainer accessPointContainer, String dataset) throws CarbonException {
		String resourceURI = accessPointContainer.getMembershipResourceURI();
		StringBuffer accessPointURIBuilder = new StringBuffer();
		//@formatter:off
		accessPointURIBuilder
			.append(resourceURI)
			.append(LDPRS.ACCESS_POINT_PREFIX)
			.append(accessPointContainer.getSlug())
		;
		//@formatter:on
		String accessPointURI = accessPointURIBuilder.toString();

		StringBuffer query = new StringBuffer();

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setDataset(dataset);
		sparqlQuery.setType(SparqlQuery.TYPE.UPDATE);

		//@formatter:off
		query
			.append("DELETE {")
				.append("\n\tGRAPH <")
					.append(resourceURI)
				.append("> {")
					.append("\n\t\t<")
						.append(accessPointURI)
					.append("> ?predicate ?object.")
					.append("\n\t\t<")
						.append(resourceURI)
					.append("> <")
						.append(LDPRS.HAS_ACCESS_POINT)
					.append("> <")
						.append(accessPointURI)
					.append(">.")
				.append("\n\t}")
			.append("\n} WHERE {")
				.append("\n\tGRAPH <")
					.append(resourceURI)
				.append("> {")
					.append("\n\t\t<")
						.append(accessPointURI)
					.append("> ?predicate ?object")
				.append("\n\t}")
			.append("\n}")
		;
		//@formatter:on

		sparqlQuery.setQuery(query.toString());

		sparqlService.update(sparqlQuery);
	}

	// TODO: Refactor method
	@PreAuthorize("hasPermission(#containerURIObject, 'DELETE')")
	public void deleteLDPContainer(URIObject containerURIObject, String dataset, String containerType, LDPContainerQueryOptions options) throws CarbonException {

		String documentURI = containerURIObject.getURI();

		boolean deleteContainer = options.includeContainerProperties();
		boolean deleteContainedResources = options.includeContainmentTriples() || options.includeContainedResources();
		boolean deleteMembershipTriples = options.includeMembershipTriples();

		if ( deleteContainer ) {
			// The container is going to be deleted (same as a simple LDPRSource)

			if ( containerType.equals(LDPC.DIRECT) || containerType.equals(LDPC.INDIRECT) ) {
				// Delete the accessPoint related to the container
				LDPContainerQueryOptions onlyContainerOptions = new LDPContainerQueryOptions(METHOD.GET);
				onlyContainerOptions.setContainerProperties(true);
				onlyContainerOptions.setContainmentTriples(false);
				onlyContainerOptions.setContainedResources(false);
				onlyContainerOptions.setMembershipTriples(false);
				onlyContainerOptions.setMemberResources(false);

				LDPContainer container = null;
				try {
					container = getLDPContainer(containerURIObject, dataset, containerType, onlyContainerOptions);
				} catch (CarbonException e) {
					// TODO: Add message to debug stack trace
					throw e;
				}

				deleteLDPRSource(containerURIObject, true, dataset);
				deleteAccessPoint(container, dataset);
			} else {
				deleteLDPRSource(containerURIObject, true, dataset);
			}

		} else {
			if ( deleteContainedResources ) {
				deleteLDPCContainedResources(documentURI, dataset);
			}
		}
		if ( deleteMembershipTriples ) {
			deleteLDPCMembershipTriples(documentURI, containerType, dataset);
		}

	}

	private void deleteLDPCContainedResources(String documentURI, String dataset) throws CarbonException {
		documentURI = documentURI.endsWith("/") ? documentURI : documentURI.concat("/");

		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		// --- Delete ContainedResources and their occurrences
		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("\nDELETE {")
				.append("\n\tGRAPH ?resourceGraph {")
					.append("\n\t\t?resourceSubject ?resourcePredicate ?resourceObject")
				.append("\n\t}")
				.append("\n\tGRAPH ?ocurrenceGraph {")
					.append("\n\t\t?ocurrenceSubject ?ocurrencePredicate ?ocurrenceObject")
				.append("\n\t}")
			.append("\n} WHERE {")
				.append("\n\tOPTIONAL {")
					.append("\n\t\tGRAPH ?resourceGraph {")
						.append("\n\t\t\t?resourceSubject ?resourcePredicate ?resourceObject")
						.append("\n\t\t\tFILTER(STRSTARTS(str(?resourceSubject), \"")
							.append(documentURI)
						.append("\") && (str(?resourceSubject) != \"")
							.append(documentURI)
						.append("\")).")
					.append("\n\t\t}")
				.append("\n\t}")
				.append("\n\tOPTIONAL {")
					.append("\n\t\tGRAPH ?ocurrenceGraph {")
						.append("\n\t\t\t?ocurrenceSubject ?ocurrencePredicate ?ocurrenceObject")
						.append("\n\t\t\tFILTER(STRSTARTS(str(?ocurrenceObject), \"")
							.append(documentURI)
						.append("\") && (str(?ocurrenceObject) != \"")
							.append(documentURI)
						.append("\")).")
					.append("\n\t\t}")
				.append("\n\t}")
			.append("\n}")
		;
		//@formatter:on
		sparqlQuery.setQuery(query.toString());
		sparqlService.update(sparqlQuery);
	}

	private void deleteLDPCMembershipTriples(String documentURI, String containerType, String dataset) throws CarbonException {
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.setType(SparqlQuery.TYPE.QUERY);
		sparqlQuery.setDataset(dataset);

		StringBuffer query = new StringBuffer();
		if ( containerType.equals(LDPC.BASIC) ) {
			//@formatter:off
			query
				.append("DELETE WHERE {")
					.append("\n\tGRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t<")
							.append(documentURI)
						.append("> <")
						.append(LDPC.DEFAULT_HAS_MEMBER_RELATION)
						.append("> ?member.")
					.append("\n\t}")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			//@formatter:off
			query
				.append("DELETE {")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?hasMemberRelation ?member")
					.append("\n\t}")
				.append("\n} WHERE {")
					.append("\n\tGRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t<")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t<")
								.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
								.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t}")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?hasMemberRelation ?member")
					.append("\n\t}")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			//@formatter:off
			query
				.append("DELETE {")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?hasMemberRelation ?member")
					.append("\n\t}")
				.append("\n} WHERE {")
					.append("\n\tGRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t<")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t<")
								.append(LDPC.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
								.append(LDPC.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t}")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?hasMemberRelation ?member")
					.append("\n\t}")
				.append("\n}")
			;
			//@formatter:on
		}

		sparqlQuery.setQuery(query.toString());
		sparqlService.update(sparqlQuery);
	}

	// ========= End: LDP-C Related Methods

	public void setRdfService(RdfService rdfService) {
		this.rdfService = rdfService;
	}

	public void setSparqlService(SparqlService sparqlService) {
		this.sparqlService = sparqlService;
	}

}
