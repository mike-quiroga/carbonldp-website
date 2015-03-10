package com.carbonldp.ldp.sources;

import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.descriptions.RDFSourceDescription;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.models.AccessPoint;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

@Transactional
public class SesameRDFSourceRepository extends AbstractSesameLDPRepository implements RDFSourceRepository {

	public SesameRDFSourceRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	private static final String exists_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "ASK {" ).append( NEW_LINE )
				.append( TAB ).append( "GRAPH ?sourceURI {" ).append( NEW_LINE )
				.append( TAB ).append( TAB ).append( "?sourceURI ?p ?o" ).append( NEW_LINE )
				.append( TAB ).append( "}" ).append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		exists_query = queryBuilder.toString();
	}

	@Override
	public boolean exists(URI sourceURI) {
		BooleanQuery query = connectionTemplate.read( (connection) -> {
			return connection.prepareBooleanQuery( QueryLanguage.SPARQL, exists_query );
		} );

		query.setBinding( "sourceURI", sourceURI );

		try {
			return query.evaluate();
		} catch ( QueryEvaluationException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}
	}

	private static final String get_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "CONSTRUCT {" ).append( NEW_LINE )
				.append( TAB ).append( "?s ?p ?o" ).append( NEW_LINE )
				.append( "} WHERE {" ).append( NEW_LINE )
				.append( TAB ).append( "GRAPH ?sourceURI {" ).append( NEW_LINE )
				.append( TAB ).append( TAB ).append( "?s ?p ?o" ).append( NEW_LINE )
				.append( TAB ).append( "}" ).append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		get_query = queryBuilder.toString();
	}

	@Override
	public RDFSource get(URI sourceURI) {
		GraphQuery query = connectionTemplate.read( (connection) -> {
			return connection.prepareGraphQuery( QueryLanguage.SPARQL, get_query );
		} );

		query.setBinding( "sourceURI", sourceURI );

		AbstractModel model = new LinkedHashModel();
		GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( model );
		handler.handleQuery( query );

		return new RDFSource( model, sourceURI );
	}

	// TODO: Decide. Should it return empty objects?
	@Override
	public Set<RDFSource> get(Set<URI> sourceURIs) {
		Resource[] contexts = sourceURIs.toArray( new Resource[sourceURIs.size()] );

		AbstractModel sourcesModel = connectionTemplate.read( (connection) -> {
			AbstractModel model = new LinkedHashModel();
			RepositoryResult<Statement> statements = connection.getStatements( null, null, null, false, contexts );
			while ( statements.hasNext() ) {
				model.add( statements.next() );
			}
			return model;
		} );

		Set<RDFSource> sources = new HashSet<RDFSource>();
		for ( URI sourceURI : sourceURIs ) {
			sources.add( new RDFSource( sourcesModel, sourceURI ) );
		}

		return sources;
	}

	@Override
	public DateTime getModified(URI sourceURI) {
		return resourceRepository.getDate( sourceURI, RDFSourceDescription.Property.MODIFIED );
	}

	private static final String getDefaultInteractionModel_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "SELECT ?dim WHERE {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( "GRAPH ?sourceURI {" )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( RDFNodeUtil.generatePredicateStatement( "?sourceURI", "?dim", RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL ) )
				.append( NEW_LINE )
				.append( TAB )
				.append( TAB )
				.append( "FILTER(isURI(?dim))." )
				.append( NEW_LINE )
				.append( TAB )
				.append( "}" )
				.append( NEW_LINE )
				.append( "}" )
				.append( NEW_LINE )
				.append( "LIMIT 1" )
		;
		//@formatter:on
		getDefaultInteractionModel_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	@Override
	public URI getDefaultInteractionModel(URI sourceURI) {
		TupleQuery query = connectionTemplate.read( (connection) -> {
			return connection.prepareTupleQuery( QueryLanguage.SPARQL, getDefaultInteractionModel_query );
		} );

		query.setBinding( "sourceURI", sourceURI );

		try {
			TupleQueryResult result = query.evaluate();
			if ( !result.hasNext() ) return ContainerDescription.Default.HAS_MEMBER_RELATION.getURI();
			else return ValueUtil.getURI( result.next().getBinding( "dim" ).getValue() );
		} catch ( QueryEvaluationException e ) {
			// TODO: Add error number
			throw new RepositoryRuntimeException( e );
		}
	}

	@Override
	public DateTime touch(URI sourceURI) {
		DateTime now = DateTime.now();
		return touch( sourceURI, now );
	}

	@Override
	public DateTime touch(URI sourceURI, DateTime modified) {
		resourceRepository.remove( sourceURI, RDFSourceDescription.Property.MODIFIED );
		resourceRepository.add( sourceURI, RDFSourceDescription.Property.MODIFIED.getURI(), modified );
		return modified;
	}

	@Override
	public void createAccessPoint(URI sourceURI, AccessPoint accessPoint) {
		documentRepository.addDocument( accessPoint.getDocument() );
		addAccessPoint( sourceURI, accessPoint );
	}

	private void addAccessPoint(URI sourceURI, AccessPoint accessPoint) {
		connectionTemplate.write( (connection) -> {
			connection.add( sourceURI, RDFSourceDescription.Property.ACCESS_POINT.getURI(), accessPoint.getURI(), sourceURI );
		} );
	}

	@Override
	public void update(RDFSource source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(URI sourceURI) {
		// TODO Auto-generated method stub

	}
}
