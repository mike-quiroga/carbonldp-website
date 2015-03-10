package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

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
	public boolean supports(Type containerType) {
		return containerType == Type.BASIC;
	}

	private static final String isMember_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "ASK {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?containerURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "?containerURI ?hasMemberRelation ?member" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		isMember_query = queryBuilder.toString();
	}

	@Override
	public boolean isMember(URI containerURI, URI possibleMemberURI) {
		return isMember( containerURI, possibleMemberURI, isMember_query );
	}

	@Override
	protected URI getMembershipResource(URI containerURI) {
		return containerURI;
	}

	private static final String getProperties_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "CONSTRUCT {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "?containerURI ?p ?o" )
				.append( NEW_LINE )
				.append( "} WHERE {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?containerURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "?containerURI ?p ?o." )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "FILTER(" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( TAB )
				.append( "(?p != ?hasMemberRelation)" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( TAB )
				.append( "&&" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( TAB )
				.append( "(?p NOT " )
				.append( RDFNodeUtil.generateINOperator( ContainerDescription.Property.CONTAINS ) )
				.append( ")" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( ")" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		getProperties_query = queryBuilder.toString();
	}

	@Override
	public Set<Statement> getProperties(URI containerURI) {
		return getProperties( containerURI, getProperties_query );
	}

	private static final String getMembershipTriples_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "CONSTRUCT {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "?containerURI ?hasMemberRelation ?members" )
				.append( NEW_LINE )
				.append( "} WHERE {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?containerURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "?containerURI ?hasMemberRelation ?members" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		getMembershipTriples_query = queryBuilder.toString();
	}

	@Override
	public Set<Statement> getMembershipTriples(URI containerURI) {
		return getMembershipTriples( containerURI, getMembershipTriples_query );
	}

	private static final String findMembers_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "SELECT ?members WHERE {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?containerURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "?containerURI ?hasMemberRelation ?members" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?members {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "%1$s" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		findMembers_query = queryBuilder.toString();
	}

	@Override
	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings) {
		return findMembers( containerURI, sparqlSelector, bindings, findMembers_query );
	}

	private static final String filterMembers_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "SELECT ?members WHERE {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?containerURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "?containerURI ?hasMemberRelation ?members." )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "%1$s" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		filterMembers_query = queryBuilder.toString();
	}

	@Override
	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs) {
		return filterMembers( containerURI, possibleMemberURIs, filterMembers_query );
	}
}
