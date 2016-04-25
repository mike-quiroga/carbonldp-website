package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.carbonldp.Consts.*;

@Transactional
public abstract class AbstractTypedContainerRepository extends AbstractSesameLDPRepository implements TypedContainerRepository {

	public AbstractTypedContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	protected boolean isMember( IRI containerIRI, IRI possibleMemberIRI, String isMember_query ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );
		bindings.put( "member", possibleMemberIRI );
		return sparqlTemplate.executeBooleanQuery( isMember_query, bindings );
	}

	protected boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings, String hasMembersQuery ) {
		String queryString = String.format( hasMembersQuery, sparqlSelector );

		bindings.put( "containerIRI", containerIRI );

		return sparqlTemplate.executeBooleanQuery( queryString, bindings );
	}

	private static final String getHasMemberRelation_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "SELECT ?hasMemberRelation WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerIRI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(isIRI(?hasMemberRelation))." ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" ).append( NEW_LINE )
			.append( "LIMIT 1" )
		;
		getHasMemberRelation_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected IRI getHasMemberRelation( IRI containerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );

		return sparqlTemplate.executeTupleQuery( getHasMemberRelation_query, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return ContainerDescription.Default.HAS_MEMBER_RELATION.getIRI();
			else return ValueUtil.getIRI( queryResult.next().getBinding( "hasMemberRelation" ).getValue() );
		} );
	}

	private static final String getMemberOfRelation_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "SELECT ?memberOfRelation WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerIRI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?memberOfRelation", ContainerDescription.Property.MEMBER_OF_RELATION ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(isIRI(?memberOfRelation))." ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" ).append( NEW_LINE )
			.append( "LIMIT 1" )
		;
		getMemberOfRelation_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected IRI getMemberOfRelation( IRI containerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );

		return sparqlTemplate.executeTupleQuery( getMemberOfRelation_query, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return null;
			else return ValueUtil.getIRI( queryResult.next().getBinding( "memberOfRelation" ).getValue() );
		} );
	}

	protected Set<Statement> getProperties( IRI containerIRI, String getProperties_query ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );

		return sparqlTemplate.executeGraphQuery( getProperties_query, bindings, queryResult -> {
			Set<Statement> statements = new HashSet<>();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
			handler.handle( queryResult );
			return statements;
		} );
	}

	protected Set<Statement> getMembershipTriples( IRI containerIRI, String getMembershipTriples_query ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );

		return sparqlTemplate.executeGraphQuery( getMembershipTriples_query, bindings, queryResult -> {
			Set<Statement> statements = new HashSet<>();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
			handler.handle( queryResult );
			return statements;
		} );
	}

	protected Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings, String findMembers_query ) {
		String queryString = String.format( findMembers_query, sparqlSelector );

		bindings.put( "containerIRI", containerIRI );

		return sparqlTemplate.executeTupleQuery( queryString, bindings, queryResult -> {
			Set<IRI> members = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "members" );
				if ( ValueUtil.isIRI( member ) ) members.add( ValueUtil.getIRI( member ) );
			}
			return members;
		} );
	}

	protected Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs, String filterMembers_query ) {
		if ( possibleMemberIRIs.isEmpty() ) return new HashSet<>();

		String queryString = String.format( filterMembers_query, SPARQLUtil.generateFilterInPlaceHolder( "?members", possibleMemberIRIs.size() ) );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );
		SPARQLUtil.addSequentialBindings( bindings, possibleMemberIRIs );

		return sparqlTemplate.executeTupleQuery( queryString, bindings, queryResult -> {
			Set<IRI> members = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "members" );
				if ( ValueUtil.isIRI( member ) ) members.add( ValueUtil.getIRI( member ) );
			}

			return members;
		} );
	}

	protected void addHasMemberRelation( IRI containerIRI, IRI memberIRI ) {
		IRI hasMemberRelation = getHasMemberRelation( containerIRI );
		IRI membershipResource = getMembershipResource( containerIRI );

		this.addHasMemberRelation( membershipResource, hasMemberRelation, memberIRI );
	}

	protected void addHasMemberRelation( IRI membershipResource, IRI hasMemberRelation, IRI memberIRI ) {
		connectionTemplate.write( connection -> connection.add( membershipResource, hasMemberRelation, memberIRI, membershipResource ) );
	}

	protected void addMemberOfRelation( IRI containerIRI, IRI member ) {
		IRI memberOfRelation = getMemberOfRelation( containerIRI );
		IRI membershipResource = getMembershipResource( containerIRI );
		if ( memberOfRelation != null ) connectionTemplate.write( connection -> connection.add( member, memberOfRelation, membershipResource, member ) );
	}

	@Override
	public void addMember( IRI containerIRI, IRI member ) {
		addHasMemberRelation( containerIRI, member );
		// TODO: check for permissions, pending design
		addMemberOfRelation( containerIRI, member );
	}

	@Override
	public void removeMember( IRI containerIRI, IRI memberIRI ) {
		IRI hasMemberRelation = getHasMemberRelation( containerIRI );
		IRI membershipResource = getMembershipResource( containerIRI );

		this.deleteMembershipTriple( membershipResource, hasMemberRelation, memberIRI );
	}

	protected void deleteMembershipTriple( IRI membershipResource, IRI hasMemberRelation, IRI memberIRI ) {
		connectionTemplate.write( connection -> connection.remove( membershipResource, hasMemberRelation, memberIRI, membershipResource ) );
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

	protected static final String getPropertiesQuery;

	static {
		Collection<IRI> values = new HashSet<>();
		values.add( RDFSourceDescription.Property.TYPE.getIRI() );
		values.add( ContainerDescription.Property.HAS_MEMBER_RELATION.getIRI() );
		values.add( ContainerDescription.Property.MEMBER_OF_RELATION.getIRI() );
		values.add( ContainerDescription.Property.MEMBERSHIP_RESOURCE.getIRI() );
		values.add( ContainerDescription.Property.INSERTED_CONTENT_RELATION.getIRI() );

		getPropertiesQuery = "" +
			"CONSTRUCT {" + NEW_LINE +
			TAB + "?containerIRI ?p ?o" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + "?containerIRI ?p ?o." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + SPARQLUtil.assignVar( "?p", values ) +
			"}"
		;
	}

}
