package com.base22.carbon.ldp;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.security.access.prepost.PreAuthorize;

import com.base22.carbon.APIPreferences.RetrieveContainerPreference;
import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerClass;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class ContainerService extends RDFSourceService {

	public abstract Container get(URIObject documentURIObject, List<RetrieveContainerPreference> preferences, String dataset) throws CarbonException;

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPRS') and hasPermission(#parentURIObject, 'CREATE_LDPC')")
	public DateTime createChildren(List<RDFSource> rdfSourceChildren, List<Container> containerChildren, URIObject parentURIObject, Container container,
			String datasetName) throws CarbonException {
		DateTime timestamp = DateTime.now();

		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);

		createRDFSourceChildren(rdfSourceChildren, parentURIObject, container, timestamp, template);
		createContainerChildren(containerChildren, parentURIObject, container, timestamp, template);
		touchRDFSource(parentURIObject, timestamp, template);

		template.execute();

		return timestamp;
	}

	protected void createChildren(List<? extends RDFSource> children, URIObject parentURIObject, Container container, DateTime timestamp,
			WriteTransactionTemplate template) throws CarbonException {
		for (RDFSource child : children) {
			child.setTimestamps(timestamp);
		}

		createChildRDFSources(children, parentURIObject, template);
		addContainments(parentURIObject, container, children, template);
	}

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPRS')")
	public DateTime createRDFSourceChildren(List<RDFSource> children, URIObject parentURIObject, Container container, String datasetName)
			throws CarbonException {
		DateTime timestamp = DateTime.now();

		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);

		createRDFSourceChildren(children, parentURIObject, container, timestamp, template);
		touchRDFSource(parentURIObject, timestamp, template);
		template.execute();

		return timestamp;
	}

	protected void createRDFSourceChildren(List<RDFSource> children, URIObject parentURIObject, Container container, DateTime timestamp,
			WriteTransactionTemplate template) throws CarbonException {
		createChildren(children, parentURIObject, container, timestamp, template);
	}

	@PreAuthorize("hasPermission(#parentURIObject, 'CREATE_LDPC')")
	public DateTime createContainerChildren(List<Container> children, URIObject parentURIObject, Container container, String datasetName)
			throws CarbonException {
		DateTime timestamp = DateTime.now();

		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);

		createContainerChildren(children, parentURIObject, container, timestamp, template);
		touchRDFSource(parentURIObject, timestamp, template);
		template.execute();

		return timestamp;
	}

	protected void createContainerChildren(List<Container> children, URIObject parentURIObject, Container container, DateTime timestamp,
			WriteTransactionTemplate template) throws CarbonException {
		createChildren(children, parentURIObject, container, timestamp, template);
	}

	protected void addContainments(URIObject containerURIObject, Container container, List<? extends RDFSource> containments, WriteTransactionTemplate template)
			throws CarbonException {
		List<Statement> containedStatements = new ArrayList<Statement>();
		for (RDFSource containment : containments) {
			Statement containedStatement = ResourceFactory.createStatement(container.getResource(), ContainerClass.Properties.CONTAINS.getProperty(),
					containment.getResource());
			containedStatements.add(containedStatement);
		}

		modelService.addStatements(containerURIObject, containedStatements, template);
		addMembers(containerURIObject, container, containments, template);
	}

	@PreAuthorize("hasPermission(#containerURIObject, 'ADD_MEMBER')")
	public void addMembers(URIObject containerURIObject, Container container, List<RDFSource> members, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		addMembers(containerURIObject, container, members, template);
		template.execute();
	}

	protected abstract void addMembers(URIObject containerURIObject, Container container, List<? extends RDFSource> members, WriteTransactionTemplate template)
			throws CarbonException;

	protected void addInverseMembershipTriples(URIObject containerURIObject, Container container, List<? extends RDFSource> members,
			WriteTransactionTemplate template) throws CarbonException {
		StringBuffer query = new StringBuffer();

		//@formatter:off
		query.append("INSERT DATA {");
		
		for(RDFSource member : members) {
			query
				.append("\n\tGRAPH <")
					.append(member.getURI())
				.append("> {")
					.append("\n\t\t<")
						.append(member.getURI())
					.append("> <")
						.append(container.getMemberOfRelation())
						.append("> <")
						.append(container.getURI())
						.append(">.")
				.append("\n\t}.")
			;
		}
		
		query.append("}");
		//@formatter:on

		sparqlService.update(query.toString(), template);
	}

}
