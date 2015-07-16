package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

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
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?member." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public boolean hasMember( URI containerURI, URI possibleMemberURI ) {
		return isMember( containerURI, possibleMemberURI, isMemberQuery );
	}

	private static final String hasMembersQuery;

	static {
		hasMembersQuery = "" +
			"ASK {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?members {" + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public boolean hasMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings ) {
		return hasMembers( containerURI, sparqlSelector, bindings, hasMembersQuery );
	}

	@Override
	protected URI getMembershipResource( URI containerURI ) {
		return containerURI;
	}

	private static final String getPropertiesQuery;

	static {
		getPropertiesQuery = "" +
			"CONSTRUCT {" + NEW_LINE +
			TAB + "?containerURI ?p ?o" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?p ?o." + NEW_LINE +
			TAB + TAB + "FILTER(" + NEW_LINE +
			TAB + TAB + TAB + "(?p != ?hasMemberRelation)" + NEW_LINE +
			TAB + TAB + TAB + "&&" + NEW_LINE +
			TAB + TAB + TAB + "(?p NOT " + RDFNodeUtil.generateINOperator( ContainerDescription.Property.CONTAINS ) + ")" + NEW_LINE +
			TAB + TAB + ")" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<Statement> getProperties( URI containerURI ) {
		return getProperties( containerURI, getPropertiesQuery );
	}

	private static final String getMembershipTriplesQuery;

	static {
		getMembershipTriplesQuery = "" +
			"CONSTRUCT {" + NEW_LINE +
			TAB + "?containerURI ?hasMemberRelation ?members" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<Statement> getMembershipTriples( URI containerURI ) {
		return getMembershipTriples( containerURI, getMembershipTriplesQuery );
	}

	private static final String findMembersQuery;

	static {
		findMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?members {" + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<URI> findMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings ) {
		return findMembers( containerURI, sparqlSelector, bindings, findMembersQuery );
	}

	private static final String filterMembersQuery;

	static {
		filterMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?members." + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs ) {
		return filterMembers( containerURI, possibleMemberURIs, filterMembersQuery );
	}

	private static final String removeMembersQuery;

	static {
		removeMembersQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?containedURI." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			TAB + "GRAPH ?containedURI {" + NEW_LINE +
			TAB + TAB + "?containedURI ?memberOfRelation ?containerURI." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + "?containerURI ?hasMemberRelation ?containedURI." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			TAB + "OPTIONAL {" + NEW_LINE +
			TAB + TAB + "GRAPH ?containedURI {" + NEW_LINE +
			TAB + TAB + TAB + "?containedURI ?memberOfRelation ?containerURI." + NEW_LINE +
			TAB + TAB + "}." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}"
		;
	}

	@Override
	public void removeMembers( URI containerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );
		sparqlTemplate.executeUpdate( removeMembersQuery, bindings );
	}
}
