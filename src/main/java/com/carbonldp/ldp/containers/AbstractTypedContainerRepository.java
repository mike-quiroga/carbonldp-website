package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

public abstract class AbstractTypedContainerRepository extends AbstractSesameLDPRepository implements TypedContainerRepository {

	public AbstractTypedContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	protected boolean isMember(URI containerURI, URI possibleMemberURI, String isMember_query) {
		RepositoryConnection connection = connectionFactory.getConnection();

		BooleanQuery query;
		try {
			query = connection.prepareBooleanQuery( QueryLanguage.SPARQL, isMember_query );
		} catch ( RepositoryException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		}

		query.setBinding( "containerURI", containerURI );
		query.setBinding( "member", possibleMemberURI );

		try {
			return query.evaluate();
		} catch ( QueryEvaluationException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}
	}

	private static final String getHasMemberRelation_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "SELECT ?hasMemberRelation WHERE {" )
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
				.append( "FILTER(isURI(?hasMemberRelation))." )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
				.append( NEW_LINE )
				.append( "LIMIT 1" )
		;
		//@formatter:on
		getHasMemberRelation_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected URI getHasMemberRelation(URI containerURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		TupleQuery query;
		try {
			query = connection.prepareTupleQuery( QueryLanguage.SPARQL, getHasMemberRelation_query );
		} catch ( RepositoryException e ) {
			// TODO: Add error number
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		}

		query.setBinding( "containerURI", containerURI );

		try {
			TupleQueryResult result = query.evaluate();
			if ( !result.hasNext() ) return ContainerDescription.Default.HAS_MEMBER_RELATION.getURI();
			else return ValueUtil.getURI( result.next().getBinding( "hasMemberRelation" ).getValue() );
		} catch ( QueryEvaluationException e ) {
			// TODO: Add error number
			throw new RepositoryRuntimeException( e );
		}
	}

	private static final String getMemberOfRelation_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "SELECT ?memberOfRelation WHERE {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?containerURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?memberOfRelation", ContainerDescription.Property.MEMBER_OF_RELATION ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "FILTER(isURI(?memberOfRelation))." )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
				.append( NEW_LINE )
				.append( "LIMIT 1" )
		;
		//@formatter:on
		getMemberOfRelation_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected URI getMemberOfRelation(URI containerURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		TupleQuery query;
		try {
			query = connection.prepareTupleQuery( QueryLanguage.SPARQL, getMemberOfRelation_query );
		} catch ( RepositoryException e ) {
			// TODO: Add error number
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		}

		query.setBinding( "containerURI", containerURI );

		try {
			TupleQueryResult result = query.evaluate();
			if ( !result.hasNext() ) return null;
			else return ValueUtil.getURI( result.next().getBinding( "memberOfRelation" ).getValue() );
		} catch ( QueryEvaluationException e ) {
			// TODO: Add error number
			throw new RepositoryRuntimeException( e );
		}
	}

	protected Set<Statement> getProperties(URI containerURI, String getProperties_query) {
		RepositoryConnection connection = connectionFactory.getConnection();

		GraphQuery query;
		try {
			query = connection.prepareGraphQuery( QueryLanguage.SPARQL, getProperties_query );
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		}

		query.setBinding( "containerURI", containerURI );

		Set<Statement> statements = new HashSet<Statement>();
		GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
		handler.handleQuery( query );

		return statements;
	}

	protected Set<Statement> getMembershipTriples(URI containerURI, String getMembershipTriples_query) {
		RepositoryConnection connection = connectionFactory.getConnection();

		GraphQuery query;
		try {
			query = connection.prepareGraphQuery( QueryLanguage.SPARQL, getMembershipTriples_query );
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		}

		query.setBinding( "containerURI", containerURI );

		Set<Statement> statements = new HashSet<Statement>();
		GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
		handler.handleQuery( query );

		return statements;
	}

	protected abstract URI getMembershipResource(URI containerURI);

	protected Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings, String findMembers_query) {
		RepositoryConnection connection = connectionFactory.getConnection();
		String queryString = String.format( findMembers_query, sparqlSelector );
		TupleQuery query;
		try {
			query = connection.prepareTupleQuery( QueryLanguage.SPARQL, queryString );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		} catch ( RepositoryException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}

		// TODO: Make ?containerURI a constant
		query.setBinding( "containerURI", containerURI );

		if ( bindings != null ) {
			for ( Entry<String, Value> binding : bindings.entrySet() ) {
				query.setBinding( binding.getKey(), binding.getValue() );
			}
		}

		Set<URI> members = new HashSet<URI>();
		try {
			TupleQueryResult result = query.evaluate();

			while ( result.hasNext() ) {
				BindingSet bindingSet = result.next();
				Value member = bindingSet.getValue( "members" );
				if ( ValueUtil.isURI( member ) ) members.add( ValueUtil.getURI( member ) );
			}

		} catch ( QueryEvaluationException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}

		return members;

	}

	protected Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs, String filterMembers_query) {
		Set<URI> members = new HashSet<URI>();
		if ( possibleMemberURIs.isEmpty() ) return members;

		RepositoryConnection connection = connectionFactory.getConnection();

		String queryString = String.format( filterMembers_query, SPARQLUtil.generateFilterInPlaceHolder( "?members", possibleMemberURIs
				.size() ) );
		TupleQuery query;
		try {
			query = connection.prepareTupleQuery( QueryLanguage.SPARQL, queryString );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		} catch ( RepositoryException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}

		// TODO: Make ?containerURI a constant
		query.setBinding( "containerURI", containerURI );

		SPARQLUtil.populateSequentialPlaceholders( query, possibleMemberURIs );

		try {
			TupleQueryResult result = query.evaluate();

			while ( result.hasNext() ) {
				BindingSet bindingSet = result.next();
				Value member = bindingSet.getValue( "members" );
				if ( ValueUtil.isURI( member ) ) members.add( ValueUtil.getURI( member ) );
			}

		} catch ( QueryEvaluationException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}

		return members;
	}

	protected void addMember(URI containerURI, URI memberURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		URI hasMemberRelation = getHasMemberRelation( containerURI );
		URI membershipResource = getMembershipResource( containerURI );

		try {
			connection.add( membershipResource, hasMemberRelation, memberURI, membershipResource );
		} catch ( RepositoryException e ) {
			// TODO: Add error number
			throw new RepositoryRuntimeException( e );
		}
	}

	protected RDFSource addMemberOfRelation(URI containerURI, RDFSource member) {
		URI memberOfRelation = getMemberOfRelation( containerURI );
		URI membershipResource = getMembershipResource( containerURI );
		if ( memberOfRelation != null ) {
			member.add( memberOfRelation, membershipResource );
		}
		return member;
	}

	@Override
	public RDFSource addMember(URI containerURI, RDFSource member) {
		addMember( containerURI, member.getURI() );
		return addMemberOfRelation( containerURI, member );
	}

}
