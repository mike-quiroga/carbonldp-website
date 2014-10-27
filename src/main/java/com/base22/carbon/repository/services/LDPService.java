package com.base22.carbon.repository.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.CarbonException;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.ldp.URIObjectDAO;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerClass;
import com.base22.carbon.ldp.models.ContainerClass.ContainerType;
import com.base22.carbon.ldp.models.ContainerFactory;
import com.base22.carbon.ldp.models.ContainerQueryOptions;
import com.base22.carbon.ldp.models.ContainerQueryOptions.METHOD;
import com.base22.carbon.ldp.models.NonRDFSourceClass;
import com.base22.carbon.ldp.models.RDFResourceClass;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.RDFSourceClass;
import com.base22.carbon.ldp.models.RDFSourceFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.ldp.models.WrapperForLDPNR;
import com.base22.carbon.ldp.models.WrapperForLDPNRFactory;
import com.base22.carbon.ldp.patch.PATCHRequest;
import com.base22.carbon.ldp.patch.PATCHService;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.base22.carbon.sparql.SPARQLService;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

@Service("ldpService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class LDPService {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private SPARQLService sparqlService;
	@Autowired
	private PATCHService patchService;
	@Autowired
	private URIObjectDAO uriObjectDAO;
	@Autowired
	private PermissionService permissionService;

	static final Logger LOG = LoggerFactory.getLogger(LDPService.class);

	public void init() {
	}

	// ========= LDP-RS Related Methods

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPRS')")
	public URIObject createChildLDPRSource(RDFSource ldpRSource, URIObject parentURIObject, String dataset) throws CarbonException {
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
			modelService.addNamedModel(documentURI, sourceModel, dataset);
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
	public RDFSource getLDPRSource(URIObject documentURIObject, String dataset) throws CarbonException {
		RDFSource ldpRSource = null;

		String documentURI = documentURIObject.getURI();

		Model model = modelService.getNamedModel(documentURI, dataset);
		model = model.difference(ModelFactory.createDefaultModel());

		RDFSourceFactory factory = new RDFSourceFactory();
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

	public RDFSource getLDPRSourceBranch(URIObject documentURIObject, String dataset) throws CarbonException {
		RDFSource source = null;

		String documentURI = documentURIObject.getURI();

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
				LOG.error("<< getLDPRSource() > The LDPRSource object couldn't be created.");
			}
			throw e;
		}

		return source;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'EXECUTE_SPARQL_QUERY')")
	public ResultSet executeSELECTonLDPRSource(URIObject documentURIObject, Query query, String dataset) throws CarbonException {
		ResultSet resultSet = null;

		RDFSource rdfSourceBranch = this.getLDPRSourceBranch(documentURIObject, dataset);

		try {
			resultSet = sparqlService.select(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return resultSet;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'EXECUTE_SPARQL_QUERY')")
	public Model executeCONSTRUCTonLDPRSource(URIObject documentURIObject, Query query, String dataset) throws CarbonException {
		Model model = null;

		RDFSource rdfSourceBranch = this.getLDPRSourceBranch(documentURIObject, dataset);

		try {
			model = sparqlService.construct(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return model;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'EXECUTE_SPARQL_QUERY')")
	public Model executeDESCRIBEonLDPRSource(URIObject documentURIObject, Query query, String dataset) throws CarbonException {
		Model model = null;

		RDFSource rdfSourceBranch = this.getLDPRSourceBranch(documentURIObject, dataset);

		try {
			model = sparqlService.describe(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return model;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'EXECUTE_SPARQL_QUERY')")
	public Boolean executeASKonLDPRSource(URIObject documentURIObject, Query query, String dataset) throws CarbonException {
		Boolean result = null;

		RDFSource rdfSourceBranch = this.getLDPRSourceBranch(documentURIObject, dataset);

		try {
			result = sparqlService.ask(query, rdfSourceBranch.getResource().getModel());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return result;
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public String getETagofLDPRSource(URIObject documentURIObject, String dataset) throws CarbonException {
		String eTag = null;

		LOG.trace(">> getETagofLDPRSource()");

		String documentURI = documentURIObject.getURI();

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
							.append(RDFSourceClass.MODIFIED)
						.append("> ?etag.")
				.append("}")
			.append("}")
		;
		//@formatter:on

		ResultSet resultSet = sparqlService.select(query.toString(), dataset);
		if ( resultSet.hasNext() ) {
			QuerySolution solution = resultSet.next();
			eTag = solution.get("etag").toString();
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- getETagofLDPRSource() > Got: {}", eTag);
			}
		}

		return eTag;
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public Set<String> getDocumentTypes(URIObject documentURIObject, String dataset) throws CarbonException {
		Set<String> documentTypes = new HashSet<String>();

		LOG.trace(">> getDocumentTypes()");

		String documentURI = documentURIObject.getURI();

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
						.append(RDFResourceClass.Properties.RDF_TYPE.getUri())
					.append("> ?type.")
				.append("}")
			.append("}")
		;
		//@formatter:on

		ResultSet resultSet = sparqlService.select(query.toString(), dataset);
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			String docType = solution.get("type").toString();
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("--  getDocumentTypes() > Got: {}", docType);
			}
			documentTypes.add(docType);
		}

		return documentTypes;
	}

	public String getRealURIofLDPRSource(String documentURI, String dataset) throws CarbonException {
		String realURI = null;

		if ( modelService.namedModelExists(documentURI, dataset) ) {
			realURI = documentURI;
		}
		documentURI = documentURI.concat("/");
		if ( modelService.namedModelExists(documentURI, dataset) ) {
			realURI = documentURI;
		}

		return realURI;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'UPDATE')")
	public DateTime touchRDFSource(URIObject documentURIObject, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		DateTime modified = touchRDFSource(documentURIObject, template);
		template.execute();
		return modified;
	}

	private DateTime touchRDFSource(URIObject documentURIObject, WriteTransactionTemplate template) throws CarbonException {
		final DateTime modified = DateTime.now();

		modelService.deleteProperty(documentURIObject, documentURIObject.getURI(), RDFSourceClass.Properties.MODIFIED.getProperty(), template);

		Resource resource = ResourceFactory.createResource(documentURIObject.getURI());
		RDFNode object = ResourceFactory.createTypedLiteral(modified.toString(), XSDDatatype.XSDdateTime);
		Statement modifiedStatement = ResourceFactory.createStatement(resource, RDFSourceClass.Properties.MODIFIED.getProperty(), object);
		modelService.addStatements(documentURIObject, Arrays.asList(modifiedStatement), template);

		return modified;
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'EXTEND')")
	public void replaceLDPRSource(RDFSource ldpRSource, URIObject documentURIObject, String dataset) throws CarbonException {
		try {
			deleteLDPRSource(documentURIObject, false, dataset);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
		// Add the namedModel to the dataset
		Model sourceModel = ldpRSource.getResource().getModel();
		try {
			modelService.addNamedModel(documentURIObject.getURI(), sourceModel, dataset);
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

		sparqlService.update(query.toString(), dataset);

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

		sparqlService.update(query.toString(), dataset);
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'UPDATE')")
	public DateTime patchRDFSource(URIObject documentURIObject, PATCHRequest patchRequest, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);

		patchService.executePATCHRequest(documentURIObject, patchRequest, datasetName, template);

		DateTime modified = touchRDFSource(documentURIObject, template);

		template.execute();

		return modified;
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
			modelService.addNamedModel(documentURI, sourceModel, dataset);
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
			model = modelService.getNamedModel(documentURI, dataset);
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

		if ( documentTypes.contains(NonRDFSourceClass.TYPE) ) {
			itIs = true;
		}

		return itIs;
	}

	// ========= End: LDP-NR Related Methods
	// ========= LDP-C Related Methods

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	public URIObject createRootContainer(Container container, String dataset) throws CarbonException {
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
			modelService.addNamedModel(documentURI, sourceModel, dataset);
		} catch (CarbonException e) {
			throw e;
		}

		return documentURIObject;
	}

	// TODO: Implement
	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPC')")
	public URIObject createChildLDPContainer(Container container, URIObject parentURIObject, String dataset) throws CarbonException {
		return createChildLDPRSource(container, parentURIObject, dataset);
	}

	// TODO: Move this to a Util Class
	public boolean documentIsContainer(Set<String> documentTypes) {
		boolean itIs = false;

		if ( documentTypes.contains(ContainerClass.BASIC) ) itIs = true;
		if ( documentTypes.contains(ContainerClass.DIRECT) ) itIs = true;
		if ( documentTypes.contains(ContainerClass.INDIRECT) ) itIs = true;

		return itIs;
	}

	// TODO: Refactor
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public boolean documentIsContainer(URIObject documentURIObject, String dataset) throws CarbonException {
		boolean itIs = false;

		String documentURI = documentURIObject.getURI();

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
						.append(RDFResourceClass.Properties.RDF_TYPE.getUri())
					.append("> ?type.")
					.append("FILTER(")
						.append("?type = <")
							.append(ContainerClass.BASIC)
						.append(">")
						.append("||")
						.append("?type = <")
							.append(ContainerClass.DIRECT)
						.append(">")
						.append("||")
						.append("?type = <")
							.append(ContainerClass.INDIRECT)
						.append(">")
					.append(")")
				.append("}")
			.append("}")
		;
		//@formatter:on

		itIs = sparqlService.ask(query.toString(), dataset);

		return itIs;
	}

	// TODO: Move this to a Util Class
	public String getDocumentContainerType(Set<String> documentTypes) {

		for (String docType : documentTypes) {
			if ( docType.equalsIgnoreCase(ContainerClass.BASIC) ) {
				return ContainerClass.BASIC;
			} else if ( docType.equalsIgnoreCase(ContainerClass.DIRECT) ) {
				return ContainerClass.DIRECT;
			} else if ( docType.equalsIgnoreCase(ContainerClass.INDIRECT) ) {
				return ContainerClass.INDIRECT;
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
						.append(RDFResourceClass.Properties.RDF_TYPE.getUri())
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

		String containerTypeURI = null;
		ResultSet resultSet = sparqlService.select(query.toString(), dataset);
		if ( resultSet.hasNext() ) {
			QuerySolution solution = resultSet.next();
			containerTypeURI = solution.get("type").toString();
		}

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
						.append(ContainerClass.DIM)
					.append("> ?model.")
				.append("}")
			.append("}")
		;
		//@formatter:on

		String interactionModelURI = null;
		ResultSet resultSet = sparqlService.select(query.toString(), dataset);
		while (resultSet.hasNext() && interactionModelURI == null) {
			QuerySolution solution = resultSet.next();
			Node node = solution.get("model").asNode();
			if ( node.isURI() ) {
				interactionModelURI = node.getURI();
			}
		}

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
	public Container getLDPContainer(URIObject documentURIObject, String dataset) throws CarbonException {
		return getLDPContainer(documentURIObject, dataset, new ContainerQueryOptions(ContainerQueryOptions.METHOD.GET));
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public Container getLDPContainer(URIObject documentURIObject, String dataset, String containerType) throws CarbonException {
		return getLDPContainer(documentURIObject, dataset, containerType, new ContainerQueryOptions(ContainerQueryOptions.METHOD.GET));
	}

	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public Container getLDPContainer(URIObject documentURIObject, String dataset, ContainerQueryOptions options) throws CarbonException {
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

		return getLDPContainer(documentURIObject, dataset, containerType.getURI(), new ContainerQueryOptions(ContainerQueryOptions.METHOD.GET));
	}

	// TODO: Refactor Method
	@PreAuthorize("hasPermission(#documentURIObject, 'READ')")
	public Container getLDPContainer(URIObject documentURIObject, String dataset, String containerType, ContainerQueryOptions options) throws CarbonException {
		Container ldpContainer = null;

		String documentURI = documentURIObject.getURI();

		String query = null;
		if ( containerType.equals(ContainerClass.BASIC) ) {
			query = prepareLDPBasicContainerQuery(documentURI, options);
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
			query = prepareLDPDirectContainerQuery(documentURI, options);
		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
			query = prepareLDPIndirectContainerQuery(documentURI, options);
		} else {
			// TODO: Finish this
			throw new CarbonException("");
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("-- getLDPContainer() > container type: {}", containerType);
		}

		Model ldpContainerModel;
		ldpContainerModel = sparqlService.construct(query, dataset);

		ContainerFactory factory = new ContainerFactory();
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

		if ( containerType.equals(ContainerClass.BASIC) ) {
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
							.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
							.append("> ?memberObjects.")
						.append("\n\t\t}")
					.append("\n\t}.")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
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
							.append(ContainerClass.CONTAINS)
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
						.append(ContainerClass.MEMBERSHIP_RESOURCE)
						.append("> ?membershipResource;")
						.append("\n\t\t\t<")
						.append(ContainerClass.HAS_MEMBER_RELATION)
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
		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
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
							.append(ContainerClass.CONTAINS)
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
						.append(ContainerClass.MEMBERSHIP_RESOURCE)
						.append("> ?membershipResource;")
						.append("\n\t\t\t<")
						.append(ContainerClass.HAS_MEMBER_RELATION)
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

		ResultSet resultSet = null;
		try {
			resultSet = sparqlService.select(query, dataset);
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
	private String prepareLDPBasicContainerQuery(String documentURI, ContainerQueryOptions options) {
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
						.append(ContainerClass.CONTAINS)
						.append("> && ?containerPredicate != <")
						.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
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
						.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
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
						.append(ContainerClass.CONTAINS)
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
						.append(ContainerClass.CONTAINS)
					.append("> ?containedObjects.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append("> <")
							.append(ContainerClass.CONTAINS)
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
						.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
					.append("> ?membershipObjects.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append("> <")
							.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
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
							.append(ContainerClass.CONTAINS)
						.append("> ?containedObjects;")
						.append("\n\t\t<")
							.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
						.append("> ?membershipObjects.")
				.append("\n} WHERE {")
					.append("\n\t\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t\t <")
							.append(documentURI)
						.append(">")
							.append("\n\t\t\t\t<")
								.append(ContainerClass.CONTAINS)
							.append("> ?containedObjects;")
							.append("\n\t\t\t\t<")
								.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
							.append("> ?membershipObjects.")
					.append("\n\t\t}.")
				.append("\n}")
			;
		}
		
		return query.toString();
	}
	//@formatter:on

	//@formatter:off
	private String prepareLDPDirectContainerQuery(String documentURI, ContainerQueryOptions options) {
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
						.append(ContainerClass.CONTAINS)
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
								.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
								.append(ContainerClass.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
							.append("\n\t\tFILTER( ?containerPredicate != <")
							.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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
					.append(ContainerClass.CONTAINS)
					.append("> ?containedObjects ")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> <")
						.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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
					.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.CONTAINS)
							.append("> ?containedObjects")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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
	private String prepareLDPIndirectContainerQuery(String documentURI, ContainerQueryOptions options) {
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
						.append(ContainerClass.CONTAINS)
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
								.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
								.append(ContainerClass.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
							.append("\n\t\tFILTER( ?containerPredicate != <")
							.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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
					.append(ContainerClass.CONTAINS)
					.append("> ?containedObjects ")
				.append("\n} WHERE {")
					.append("\n\t GRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t <")
							.append(documentURI)
						.append("> <")
						.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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
					.append(ContainerClass.CONTAINS)
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
							.append(ContainerClass.CONTAINS)
							.append("> ?containedObjects")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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
		if ( containerType.equals(ContainerClass.BASIC) ) {
			//@formatter:off
			query
				.append("ASK {")
					.append("\n\tGRAPH <")
						.append(containerURI)
					.append("> {")
						.append("\n\t\t<")
							.append(containerURI)
						.append("> <")
						.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
						.append("> <")
							.append(memberURI)
						.append(">.")
					.append("\n\t}")
				.append("\n\t}")
			;
			//@formatter:on

			isMember = sparqlService.ask(query.toString(), dataset);
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
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
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
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

			ResultSet resultSet = sparqlService.select(query.toString(), dataset);
			isMember = resultSet.getRowNumber() >= 0;

		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
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
							.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
							.append(ContainerClass.HAS_MEMBER_RELATION)
							.append("> ?membershipRelation;")
							.append("\n\t\t\t<")
							.append(ContainerClass.ICR)
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

			ResultSet resultSet = sparqlService.select(query.toString(), dataset);
			isMember = resultSet.getRowNumber() >= 1;
		}

		return isMember;
	}

	// TODO: Decide. Should we make this private?
	public void addDocumentAsContainment(Container container, RDFSource document, String dataset) throws CarbonException {
		String containerType = container.getTypeOfContainer();

		StringBuffer query = new StringBuffer();

		if ( containerType.equals(ContainerClass.BASIC) ) {
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
								.append(ContainerClass.CONTAINS)
								.append("> <")
								.append(document.getURI())
								.append(">;")
							.append("\n\t\t\t<")
								.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
								.append("> <")
								.append(document.getURI())
								.append(">.")
					.append("\n\t}")
				.append("}")
			;
			//@formatter:on
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
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
								.append(ContainerClass.CONTAINS)
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
		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
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
								.append(ContainerClass.CONTAINS)
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

		sparqlService.update(query.toString(), dataset);

	}

	// TODO: Decide. How are we going to relate a URIObject with a LDPContainer object?
	public void addDocumentAsMember(Container container, RDFSource document, String dataset) throws CarbonException {
		String containerType = container.getTypeOfContainer();

		StringBuffer query = new StringBuffer();

		if ( containerType.equals(ContainerClass.BASIC) ) {
			//@formatter:off
			query
				.append("INSERT DATA {")
					.append("\n\tGRAPH <")
						.append(container.getURI())
					.append("> {")
						.append("\n\t\t<")
							.append(container.getURI())
						.append("> <")
							.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
							.append("> <")
							.append(document.getURI())
							.append(">.")
					.append("\n\t}")
				.append("}")
			;
			//@formatter:on
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
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
		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
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

		sparqlService.update(query.toString(), dataset);
	}

	public void addInverseMembershipTriple(Container container, String memberURI, String dataset) throws CarbonException {

		StringBuffer query = new StringBuffer();

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

		sparqlService.update(query.toString(), dataset);
	}

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_ACCESS_POINT')")
	public URIObject createAccessPoint(Container accessPointContainer, URIObject parentURIObject, String dataset) throws CarbonException {
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
	public void addAccessPoint(Container accessPointContainer, URIObject parentURIObject, String dataset) throws CarbonException {
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

	private void deleteAccessPoint(Container accessPointContainer, String dataset) throws CarbonException {
		String resourceURI = accessPointContainer.getMembershipResourceURI();
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
						.append(RDFSourceClass.HAS_ACCESS_POINT)
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

		sparqlService.update(query.toString(), dataset);
	}

	// TODO: Refactor method
	@PreAuthorize("hasPermission(#containerURIObject, 'DELETE')")
	public void deleteLDPContainer(URIObject containerURIObject, String dataset, String containerType, ContainerQueryOptions options) throws CarbonException {

		String documentURI = containerURIObject.getURI();

		boolean deleteContainer = options.includeContainerProperties();
		boolean deleteContainedResources = options.includeContainmentTriples() || options.includeContainedResources();
		boolean deleteMembershipTriples = options.includeMembershipTriples();

		ContainerQueryOptions onlyContainerOptions = new ContainerQueryOptions(METHOD.GET);
		onlyContainerOptions.setContainerProperties(true);
		onlyContainerOptions.setContainmentTriples(true);
		onlyContainerOptions.setContainedResources(false);
		onlyContainerOptions.setMembershipTriples(false);
		onlyContainerOptions.setMemberResources(false);

		Container container = null;
		try {
			container = getLDPContainer(containerURIObject, dataset, containerType, onlyContainerOptions);
		} catch (CarbonException e) {
			// TODO: Add message to debug stack trace
			throw e;
		}

		if ( deleteContainer ) {
			// The container is going to be deleted (same as a simple LDPRSource)

			if ( containerType.equals(ContainerClass.DIRECT) || containerType.equals(ContainerClass.INDIRECT) ) {
				// Delete the accessPoint related to the container
				deleteLDPRSource(containerURIObject, true, dataset);
				deleteAccessPoint(container, dataset);
			} else {
				deleteLDPRSource(containerURIObject, true, dataset);
			}

		} else {
			if ( deleteContainedResources ) {
				List<String> containedResourceURIs = Arrays.asList(container.listContainedResourceURIs());
				if ( containedResourceURIs.size() != 0 ) {
					List<URIObject> containedResourceURIObjects = uriObjectDAO.getByURIs(containedResourceURIs);
					for (URIObject containedResourceURIObject : containedResourceURIObjects) {
						deleteLDPRSource(containedResourceURIObject, true, dataset);
					}
				}
			}
		}
		if ( deleteMembershipTriples ) {
			deleteLDPCMembershipTriples(documentURI, containerType, dataset);
		}

	}

	private void deleteLDPCContainedResources(String documentURI, String dataset) throws CarbonException {
		documentURI = documentURI.endsWith("/") ? documentURI : documentURI.concat("/");

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

		sparqlService.update(query.toString(), dataset);
	}

	private void deleteLDPCMembershipTriples(String documentURI, String containerType, String dataset) throws CarbonException {
		StringBuffer query = new StringBuffer();
		if ( containerType.equals(ContainerClass.BASIC) ) {
			//@formatter:off
			query
				.append("DELETE WHERE {")
					.append("\n\tGRAPH <")
						.append(documentURI)
					.append("> {")
						.append("\n\t\t<")
							.append(documentURI)
						.append("> <")
						.append(ContainerClass.DEFAULT_HAS_MEMBER_RELATION)
						.append("> ?member.")
					.append("\n\t}")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
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
								.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
								.append(ContainerClass.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t}")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?hasMemberRelation ?member")
					.append("\n\t}")
				.append("\n}")
			;
			//@formatter:on
		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
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
								.append(ContainerClass.MEMBERSHIP_RESOURCE)
							.append("> ?membershipResource;")
							.append("\n\t\t\t<")
								.append(ContainerClass.HAS_MEMBER_RELATION)
							.append("> ?hasMemberRelation.")
					.append("\n\t}")
					.append("\n\tGRAPH ?membershipResource {")
						.append("\n\t\t?membershipResource ?hasMemberRelation ?member")
					.append("\n\t}")
				.append("\n}")
			;
			//@formatter:on
		}

		sparqlService.update(query.toString(), dataset);
	}

	// ========= End: LDP-C Related Methods

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public void setSparqlService(SPARQLService sparqlService) {
		this.sparqlService = sparqlService;
	}

}