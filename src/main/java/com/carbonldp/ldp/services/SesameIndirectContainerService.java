package com.carbonldp.ldp.services;

import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

@Transactional
public class SesameIndirectContainerService extends AbstractAccessPointService {

	public SesameIndirectContainerService(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
			RDFDocumentRepository documentRepository) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public boolean supports(Type containerType) {
		return containerType == Type.INDIRECT;
	}

	@Override
	public boolean isMember(URI containerURI, URI possibleMemberURI) {
		// TODO: Implement
		throw new NotImplementedException( "Not implemented." );
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
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?membershipResource {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "membershipResource ?hasMemberRelation ?members" )
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
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?membershipResource {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "membershipResource ?hasMemberRelation ?members." )
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
