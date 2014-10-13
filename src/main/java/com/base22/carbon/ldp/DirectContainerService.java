package com.base22.carbon.ldp;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.base22.carbon.APIPreferences.RetrieveContainerPreference;
import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerClass;
import com.base22.carbon.ldp.models.ContainerFactory;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.hp.hpl.jena.rdf.model.Model;

@Service("s_DirectContainerService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class DirectContainerService extends ContainerService {
	@Override
	public Container get(URIObject documentURIObject, List<RetrieveContainerPreference> preferences, String dataset) throws CarbonException {
		Container container = null;

		String documentURI = documentURIObject.getURI();

		String query = null;
		query = prepareDirectContainerQuery(documentURI, preferences);

		Model containerModel;
		containerModel = sparqlService.construct(query, dataset);

		ContainerFactory factory = new ContainerFactory();
		try {
			container = factory.create(documentURI, containerModel);
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< get() > The DirectContainer couldn't be fetched.");
			}
			throw e;
		}

		return container;
	}

	@Override
	protected void addMembers(URIObject containerURIObject, Container container, List<? extends RDFSource> members, WriteTransactionTemplate template)
			throws CarbonException {
		StringBuffer query = new StringBuffer();
		//@formatter:off
		query.append("INSERT DATA {");
		
		for(RDFSource member : members) {
			query
				.append("\n\tGRAPH <")
					.append(container.getMembershipResourceURI())
				.append("> {")
					.append("\n\t\t<")
						.append(container.getMembershipResourceURI())
					.append("> <")
						.append(container.getMembershipTriplesPredicate())
					.append("> <")
						.append(member.getURI())
					.append(">.")
				.append("\n\t}.")
			;
		}
		
		query.append("}");
		//@formatter:on

		sparqlService.update(query.toString(), template);

		if ( container.getMemberOfRelation() != null ) {
			addInverseMembershipTriples(containerURIObject, container, members, template);
		}
	}

	protected Model getContainerProperties(URIObject containerURIObject, String datasetName) throws CarbonException {
		String containerURI = containerURIObject.getURI();

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("CONSTRUCT {")
				.append("\n\t<")
					.append(containerURI)
				.append("> ?containerPredicate ?containerObject")
			.append("\n} WHERE {")
				.append("\n\t GRAPH <")
					.append(containerURI)
				.append("> {")
					.append("\n\t\t <")
						.append(containerURI)
					.append("> ?containerPredicate ?containerObject.")
					.append("\n\t\tFILTER( ?containerPredicate != <")
					.append(ContainerClass.CONTAINS)
					.append(">)")
				.append("\n\t}")
			.append("\n}")
		;
		//@formatter:on

		return sparqlService.construct(query.toString(), datasetName);
	}

	protected Model getContainmentTriples(URIObject containerURIObject, String datasetName) throws CarbonException {
		String containerURI = containerURIObject.getURI();

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("CONSTRUCT {")
				.append("\n\t<")
					.append(containerURI)
				.append("> <")
				.append(ContainerClass.CONTAINS)
				.append("> ?containedObjects ")
			.append("\n} WHERE {")
				.append("\n\t GRAPH <")
					.append(containerURI)
				.append("> {")
					.append("\n\t\t <")
						.append(containerURI)
					.append("> <")
					.append(ContainerClass.CONTAINS)
					.append("> ?containedObjects ")
				.append("\n\t}")
			.append("\n}")
		;
		//@formatter:on

		return sparqlService.construct(query.toString(), datasetName);
	}

	protected Model getMembershipTriples(URIObject containerURIObject, String datasetName) throws CarbonException {
		String containerURI = containerURIObject.getURI();

		StringBuffer query = new StringBuffer();
		//@formatter:off
		query
			.append("CONSTRUCT {")
				.append("\n\t?membershipResource ?hasMemberRelation ?members.")
			.append("\n} WHERE {")
				.append("\n\t\t GRAPH <")
					.append(containerURI)
				.append("> {")
					.append("\n\t\t\t <")
						.append(containerURI)
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
		//@formatter:on

		return sparqlService.construct(query.toString(), datasetName);
	}

	//@formatter:off
    protected String prepareDirectContainerQuery(String documentURI, List<RetrieveContainerPreference> preferences) {
        StringBuffer query = new StringBuffer();
        
        boolean containerProperties = preferences.contains(RetrieveContainerPreference.CONTAINER_PROPERTIES);
        boolean containmentTriples = preferences.contains(RetrieveContainerPreference.CONTAINMENT_TRIPLES);
        boolean membershipTriples = preferences.contains(RetrieveContainerPreference.MEMBERSHIP_TRIPLES);
        
        if( containerProperties && ! containmentTriples && ! membershipTriples ) {
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
						.append(">)")
					.append("\n\t}")
				.append("\n}")
			;
        
        } else if( containerProperties && containmentTriples && !membershipTriples ) {
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
					.append("\n\t}")
				.append("\n}")
			;
        
        } else if( containerProperties && !containmentTriples && membershipTriples ) {
            // TRUE && FALSE && TRUE
            // Container properties and membership triples
            
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
        
        } else if( containerProperties && containmentTriples && membershipTriples ) {
            // TRUE && TRUE && TRUE
            // Container properties, containment triples and membership triples
            
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
        
        } else if( !containerProperties && !containmentTriples && !membershipTriples ) {
            // FALSE && FALSE && FALSE
            
            // Bad combination, you are asking for something empty
            return null;
        
        } else if( !containerProperties && containmentTriples && !membershipTriples ) {
            // FALSE && TRUE && FALSE
            // Containment triples
            
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
        
        } else if( !containerProperties && !containmentTriples && membershipTriples ) {
            // FALSE && FALSE && TRUE
            // Membership triples
            
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
        
        } else if( !containerProperties && containmentTriples && membershipTriples ) {
            // FALSE && TRUE && TRUE
            // Containment triples and Membership triples
            
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

}
