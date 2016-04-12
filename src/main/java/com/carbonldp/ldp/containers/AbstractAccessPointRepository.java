package com.carbonldp.ldp.containers;

import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
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
public abstract class AbstractAccessPointRepository extends AbstractTypedContainerRepository {

	public AbstractAccessPointRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	private static final String getMembershipResource_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "SELECT ?membershipResource WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerIRI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(isIRI(?membershipResource))." ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" ).append( NEW_LINE )
			.append( "LIMIT 1" )
		;
		getMembershipResource_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	@Override
	public IRI getMembershipResource( IRI containerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );
		return sparqlTemplate.executeTupleQuery( getMembershipResource_query, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return null;
			else return ValueUtil.getIRI( queryResult.next().getBinding( "membershipResource" ).getValue() );
		} );
	}

	@Override
	public Set<Statement> getProperties( IRI containerIRI ) {
		return getProperties( containerIRI, getPropertiesQuery );
	}

	private static final String getMembershipTriplesQuery;

	static {
		getMembershipTriplesQuery = "" +
			"CONSTRUCT {" + NEW_LINE +
			TAB + "?membershipResource ?hasMemberRelation ?members" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "?membershipResource ?hasMemberRelation ?members" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<Statement> getMembershipTriples( IRI containerIRI ) {
		return getMembershipTriples( containerIRI, getMembershipTriplesQuery );
	}

	private static final String removeMembersQuery;

	static {
		removeMembersQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "?membershipResource ?hasMemberRelation ?member" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?member {" + NEW_LINE +
			TAB + TAB + "?member ?memberOfRelation ?container" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?container {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerIRI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?container", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "?membershipResource ?hasMemberRelation ?member" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "OPTIONAL {" + NEW_LINE +
			TAB + TAB + "GRAPH ?container {" + NEW_LINE +
			TAB + TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?container", "?memberOfRelation", ContainerDescription.Property.MEMBER_OF_RELATION ) + NEW_LINE +
			TAB + TAB + "}" + NEW_LINE +
			TAB + TAB + "GRAPH ?member {" + NEW_LINE +
			TAB + TAB + TAB + "?member ?memberOfRelation ?container" + NEW_LINE +
			TAB + TAB + "}" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public void removeMembers( IRI containerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "container", containerIRI );
		sparqlTemplate.executeUpdate( removeMembersQuery, bindings );
	}
}
