package com.carbonldp.ldp.containers;

import com.carbonldp.exceptions.IllegalArgumentException;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.*;

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
	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI ) {
		// TODO: Implement
		throw new NotImplementedException( "Not implemented." );
	}

	@Override
	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	private static final String findMembersQuery;

	static {
		findMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
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
	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings ) {
		return findMembers( containerIRI, sparqlSelector, bindings, findMembersQuery );
	}

	private static final String filterMembersQuery;

	static {
		filterMembersQuery = "" +
			"SELECT ?members WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) + NEW_LINE +
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
			TAB + "GRAPH ?containerIRI{" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?insertedContentRelation", ContainerDescription.Property.INSERTED_CONTENT_RELATION ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?memberIRI{" + NEW_LINE +
			TAB + TAB + "?memberIRI ?insertedContentRelation ?membershipObject" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}";
	}

	@Override
	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs ) {
		return filterMembers( containerIRI, possibleMemberIRIs, filterMembersQuery );
	}

	@Override
	public void addMember( IRI containerIRI, IRI member ) {
		IRI membershipObject = getMembershipObject( containerIRI, member );
		addHasMemberRelation( containerIRI, membershipObject );
		// TODO: check for permissions, pending design
		addMemberOfRelation( containerIRI, member );
	}

	@Override
	public void removeMember( IRI containerIRI, IRI memberIRI ) {
		throw new NotImplementedException( "Remove indirect container members is not implemented yet" );
	}

	private IRI getMembershipObject( IRI containerIRI, IRI member ) {
		String queryString = getMembershipObjectQuery;

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );
		bindings.put( "memberIRI", member );

		return sparqlTemplate.executeTupleQuery( queryString, bindings, queryResult -> {
			Value membershipObject;
			if ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				membershipObject = bindingSet.getValue( "membershipObject" );
				if ( queryResult.hasNext() ) throw new IllegalArgumentException( new Infraction( 0x2004, "property", "The membership object" ) );
			} else throw new IllegalArgumentException( 0x2105 );

			if ( ! ValueUtil.isIRI( membershipObject ) ) throw new IllegalArgumentException( new Infraction( 0x2005, "property", "The primary topic" ) );

			return ValueUtil.getIRI( membershipObject );
		} );
	}

}
