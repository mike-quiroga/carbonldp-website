package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.*;

@Transactional
public class SesameBasicContainerRepository extends AbstractTypedContainerRepository {

	public SesameBasicContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public boolean supports( Type containerType ) {
		return containerType == Type.BASIC;
	}

	private static final String isMemberQuery;

	static {
		isMemberQuery = "" +
			"ASK {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?member." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI ) {
		return isMember( containerIRI, possibleMemberIRI, isMemberQuery );
	}

	private static final String hasMembersQuery;

	static {
		hasMembersQuery = "" +
			"ASK {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?members {" + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings ) {
		return hasMembers( containerIRI, sparqlSelector, bindings, hasMembersQuery );
	}

	@Override
	public IRI getMembershipResource( IRI containerIRI ) {
		return containerIRI;
	}

	@Override
	public Set<Statement> getProperties( IRI containerIRI ) {
		return getProperties( containerIRI, getPropertiesQuery );
	}

	private static final String getMembershipTriplesQuery;

	static {
		getMembershipTriplesQuery = "" +
			"CONSTRUCT {" + NEW_LINE +
			TAB + "?containerIRI ?hasMemberRelation ?members" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<Statement> getMembershipTriples( IRI containerIRI ) {
		return getMembershipTriples( containerIRI, getMembershipTriplesQuery );
	}

	private static final String findMembersQuery;

	static {
		findMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?members {" + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings ) {
		return findMembers( containerIRI, sparqlSelector, bindings, findMembersQuery );
	}

	private static final String filterMembersQuery;

	static {
		filterMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs ) {
		return filterMembers( containerIRI, possibleMemberIRIs, filterMembersQuery );
	}

	private static final String removeMembersQuery;

	static {
		removeMembersQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?containedIRI." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			TAB + "GRAPH ?containedIRI {" + NEW_LINE +
			TAB + TAB + "?containedIRI ?memberOfRelation ?containerIRI." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerIRI ?hasMemberRelation ?containedIRI." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			TAB + "OPTIONAL {" + NEW_LINE +
			TAB + TAB + "GRAPH ?containedIRI {" + NEW_LINE +
			TAB + TAB + TAB + "?containedIRI ?memberOfRelation ?containerIRI." + NEW_LINE +
			TAB + TAB + "}." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}"
		;
	}

	@Override
	public void removeMembers( IRI containerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );
		sparqlTemplate.executeUpdate( removeMembersQuery, bindings );
	}
}
