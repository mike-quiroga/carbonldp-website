package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import org.apache.commons.lang3.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

@Transactional
public class SesameIndirectContainerRepository extends AbstractAccessPointRepository {

	public SesameIndirectContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public boolean supports( Type containerType ) {
		return containerType == Type.INDIRECT;
	}

	@Override
	public boolean hasMember( URI containerURI, URI possibleMemberURI ) {
		// TODO: Implement
		throw new NotImplementedException( "Not implemented." );
	}

	@Override
	public boolean hasMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	private static final String findMembersQuery;

	static {
		findMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerURI {" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "membershipResource ?hasMemberRelation ?members" + NEW_LINE +
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
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?membershipResource {" + NEW_LINE +
			TAB + TAB + "membershipResource ?hasMemberRelation ?members." + NEW_LINE +
			TAB + TAB + "%1$s" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	private static final String getMembershipObjectQuery;

	static {
		getMembershipObjectQuery = "" +
			"SELECT ?membershipObject WHERE{" + NEW_LINE +
			TAB + "GRAPH ?containerURI{" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerURI", "?insertedContentRelation", ContainerDescription.Property.INSERTED_CONTENT_RELATION ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?memberURI{" + NEW_LINE +
			TAB + TAB + "?memberURI ?insertedContentRelation ?membershipObject" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}";
	}

	@Override
	public Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs ) {
		return filterMembers( containerURI, possibleMemberURIs, filterMembersQuery );
	}

	@Override
	public void addMember( URI containerURI, URI member ) {
		URI membershipObject = getMembershipObject( containerURI, member );
		addHasMemberRelation( containerURI, membershipObject );
		// TODO: check for permissions, pending design
		//addMemberOfRelation( containerURI, member );
	}

	private URI getMembershipObject( URI containerURI, URI member ) {
		String queryString = getMembershipObjectQuery;

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );
		bindings.put( "memberURI", member );

		return sparqlTemplate.executeTupleQuery( queryString, bindings, queryResult -> {
			Value membershipObject;
			if ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				membershipObject = bindingSet.getValue( "membershipObject" );
				if ( queryResult.hasNext() ) throw new BadRequestException( "The resource has more than one membershipObject" );
				// TODO: Design which error should be thrown in here
			} else throw new RuntimeException( "The resource does not have enough information to be an indirect container member" );

			// TODO: Design which error should be thrown in here
			if ( ! ValueUtil.isURI( membershipObject ) ) throw new RuntimeException( "primary topic is nor a URI" );

			return ValueUtil.getURI( membershipObject );
		} );
	}

}
