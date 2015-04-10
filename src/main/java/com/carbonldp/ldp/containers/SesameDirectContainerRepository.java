package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

@Transactional
public class SesameDirectContainerRepository extends AbstractAccessPointRepository {

	public SesameDirectContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public boolean supports( Type containerType ) {
		return containerType == Type.DIRECT;
	}

	private static final String isMemberQuery;

	static {
		isMemberQuery = "" +
			"ASK {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "?membershipResource ?hasMemberRelation ?member" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public boolean isMember( URI containerURI, URI possibleMemberURI ) {
		return isMember( containerURI, possibleMemberURI, isMemberQuery );
	}

	private static final String findMembersQuery;

	static {
		findMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + getHasMemberRelationSPARQL( "?containerURI", "?hasMemberRelation", 2 ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "?membershipResource ?hasMemberRelation ?members" + NEW_LINE +
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
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "?membershipResource ?hasMemberRelation ?members." + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs ) {
		return filterMembers( containerURI, possibleMemberURIs, filterMembersQuery );
	}
}
