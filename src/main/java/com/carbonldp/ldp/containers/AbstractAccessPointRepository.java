package com.carbonldp.ldp.containers;

import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
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
			.append( TAB ).append( "GRAPH ?containerURI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(isURI(?membershipResource))." ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" ).append( NEW_LINE )
			.append( "LIMIT 1" )
		;
		getMembershipResource_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected URI getMembershipResource( URI containerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );
		return sparqlTemplate.executeTupleQuery( getMembershipResource_query, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return null;
			else return ValueUtil.getURI( queryResult.next().getBinding( "membershipResource" ).getValue() );
		} );
	}

	private static final String getProperties_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "CONSTRUCT {" ).append( NEW_LINE )
			.append( TAB ).append( "?containerURI ?p ?o" ).append( NEW_LINE )
			.append( "} WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerURI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "?containerURI ?p ?o." ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "FILTER(" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( TAB ).append( "(?p NOT " )
			.append( RDFNodeUtil.generateINOperator( ContainerDescription.Property.CONTAINS ) )
			.append( ")" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( ")" ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" )
		;
		getProperties_query = queryBuilder.toString();
	}

	@Override
	public Set<Statement> getProperties( URI containerURI ) {
		return getProperties( containerURI, getProperties_query );
	}

	private static final String getMembershipTriples_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "CONSTRUCT {" ).append( NEW_LINE )
			.append( TAB ).append( "?membershipResource ?hasMemberRelation ?members" ).append( NEW_LINE )
			.append( "} WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerURI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?membershipResource {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( "?membershipResource ?hasMemberRelation ?members" ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" )
		;
		getMembershipTriples_query = queryBuilder.toString();
	}

	@Override
	public Set<Statement> getMembershipTriples( URI containerURI ) {
		return getMembershipTriples( containerURI, getMembershipTriples_query );
	}
}
