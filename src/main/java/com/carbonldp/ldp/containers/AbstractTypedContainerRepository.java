package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

@Transactional
public abstract class AbstractTypedContainerRepository extends AbstractSesameLDPRepository implements TypedContainerRepository {

	public AbstractTypedContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	protected boolean isMember( URI containerURI, URI possibleMemberURI, String isMember_query ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );
		bindings.put( "member", possibleMemberURI );
		return sparqlTemplate.executeBooleanQuery( isMember_query, bindings );
	}

	protected boolean hasMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings, String hasMembersQuery ) {
		String queryString = String.format( hasMembersQuery, sparqlSelector );

		bindings.put( "containerURI", containerURI );

		return sparqlTemplate.executeBooleanQuery( queryString, bindings );
	}

	private static final String getHasMemberRelation_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "SELECT ?hasMemberRelation WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerURI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(isURI(?hasMemberRelation))." ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" ).append( NEW_LINE )
			.append( "LIMIT 1" )
		;
		getHasMemberRelation_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected URI getHasMemberRelation( URI containerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );

		return sparqlTemplate.executeTupleQuery( getHasMemberRelation_query, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return ContainerDescription.Default.HAS_MEMBER_RELATION.getURI();
			else return ValueUtil.getURI( queryResult.next().getBinding( "hasMemberRelation" ).getValue() );
		} );
	}

	private static final String getMemberOfRelation_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "SELECT ?memberOfRelation WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerURI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?memberOfRelation", ContainerDescription.Property.MEMBER_OF_RELATION ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(isURI(?memberOfRelation))." ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" ).append( NEW_LINE )
			.append( "LIMIT 1" )
		;
		getMemberOfRelation_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected URI getMemberOfRelation( URI containerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );

		return sparqlTemplate.executeTupleQuery( getMemberOfRelation_query, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return null;
			else return ValueUtil.getURI( queryResult.next().getBinding( "memberOfRelation" ).getValue() );
		} );
	}

	protected Set<Statement> getProperties( URI containerURI, String getProperties_query ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );

		return sparqlTemplate.executeGraphQuery( getProperties_query, bindings, queryResult -> {
			Set<Statement> statements = new HashSet<>();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
			handler.handle( queryResult );
			return statements;
		} );
	}

	protected Set<Statement> getMembershipTriples( URI containerURI, String getMembershipTriples_query ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );

		return sparqlTemplate.executeGraphQuery( getMembershipTriples_query, bindings, queryResult -> {
			Set<Statement> statements = new HashSet<>();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
			handler.handle( queryResult );
			return statements;
		} );
	}

	protected Set<URI> findMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings, String findMembers_query ) {
		String queryString = String.format( findMembers_query, sparqlSelector );

		bindings.put( "containerURI", containerURI );

		return sparqlTemplate.executeTupleQuery( queryString, bindings, queryResult -> {
			Set<URI> members = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "members" );
				if ( ValueUtil.isURI( member ) ) members.add( ValueUtil.getURI( member ) );
			}
			return members;
		} );
	}

	protected Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs, String filterMembers_query ) {
		if ( possibleMemberURIs.isEmpty() ) return new HashSet<>();

		String queryString = String.format( filterMembers_query, SPARQLUtil.generateFilterInPlaceHolder( "?members", possibleMemberURIs.size() ) );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );
		SPARQLUtil.addSequentialBindings( bindings, possibleMemberURIs );

		return sparqlTemplate.executeTupleQuery( queryString, bindings, queryResult -> {
			Set<URI> members = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "members" );
				if ( ValueUtil.isURI( member ) ) members.add( ValueUtil.getURI( member ) );
			}

			return members;
		} );
	}

	protected void addHasMemberRelation( URI containerURI, URI memberURI ) {
		URI hasMemberRelation = getHasMemberRelation( containerURI );
		URI membershipResource = getMembershipResource( containerURI );

		this.addHasMemberRelation( membershipResource, hasMemberRelation, memberURI );
	}

	protected void addHasMemberRelation( URI membershipResource, URI hasMemberRelation, URI memberURI ) {
		connectionTemplate.write( connection -> connection.add( membershipResource, hasMemberRelation, memberURI, membershipResource ) );
	}

	protected void addMemberOfRelation( URI containerURI, URI member ) {
		URI memberOfRelation = getMemberOfRelation( containerURI );
		URI membershipResource = getMembershipResource( containerURI );

	}

	@Override
	public void addMember( URI containerURI, URI member ) {
		addHasMemberRelation( containerURI, member );
		// TODO: check for permissions, pending design
		//addMemberOfRelation( containerURI, member );
	}

	@Override
	public void removeMember( URI containerURI, URI memberURI ) {
		URI hasMemberRelation = getHasMemberRelation( containerURI );
		URI membershipResource = getMembershipResource( containerURI );

		this.deleteMembershipTriple( membershipResource, hasMemberRelation, memberURI );

	}

	protected void deleteMembershipTriple( URI membershipResource, URI hasMemberRelation, URI memberURI ) {
		connectionTemplate.write( connection -> connection.remove( membershipResource, hasMemberRelation, memberURI, membershipResource ) );
	}

	protected static String getHasMemberRelationSPARQL( String containerVar, String hasMemberRelationVar, int numberOfTabs ) {
		String tabs = SPARQLUtil.createTabs( numberOfTabs );

		String sparql;
		sparql = "# GET ldp:hasMemberRelation or bind default values" + NEW_LINE +
			tabs + "OPTIONAL {" + NEW_LINE +
			tabs + TAB + SPARQLUtil.assignVar( "?hasMemberRelationPredicate", ContainerDescription.Property.HAS_MEMBER_RELATION ) + NEW_LINE +
			tabs + TAB + containerVar + " ?hasMemberRelationPredicate ?hmr" + NEW_LINE +
			tabs + "}" + NEW_LINE +
			tabs + SPARQLUtil.assignVar( "?defaultHasMemberRelation", ContainerDescription.Default.HAS_MEMBER_RELATION ) + NEW_LINE +
			tabs + "BIND( IF( BOUND( ?hmr ), ?hmr, ?defaultHasMemberRelation ) AS " + hasMemberRelationVar + ")"
		;
		return sparql;
	}

}
