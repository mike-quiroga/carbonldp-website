package com.carbonldp.jobs;

import com.carbonldp.jobs.ExecutionDescription.Status;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.*;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.carbonldp.Consts.*;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameExecutionRepository extends AbstractSesameLDPRepository implements ExecutionRepository {
	public SesameExecutionRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public Status getExecutionStatus( URI executionURI ) {
		RepositoryResult<Statement> statements;
		Statement statement;

		try {
			statements = connectionFactory.getConnection().getStatements( executionURI, ExecutionDescription.Property.STATUS.getURI(), null, false, executionURI );
			if ( ! statements.hasNext() ) throw new RuntimeException( "execution does not have a status" );
			statement = statements.next();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

		Value object = statement.getObject();
		if ( ! ValueUtil.isURI( object ) ) throw new RuntimeException( "job status is an invalid type" );
		URI statusURI = ValueUtil.getURI( object );

		for ( Status status : Status.values() ) {
			if ( status.getURI().equals( statusURI ) ) return status;
		}
		throw new RuntimeException( "invalid status" );
	}

	@Override
	public void changeExecutionStatus( URI executionURI, Status status ) {
		try {
			connectionFactory.getConnection().remove( executionURI, ExecutionDescription.Property.STATUS.getURI(), null, executionURI );
			connectionFactory.getConnection().add( executionURI, ExecutionDescription.Property.STATUS.getURI(), status.getURI(), executionURI );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public void enqueue( URI executionURI, URI appURI ) {
		BNode newQueueMemberBNode = connectionFactory.getConnection().getValueFactory().createBNode();
		Resource lastQueueMemberSubject = getLastMemberBNode( appURI );

		try {
			connectionFactory.getConnection().add( newQueueMemberBNode, RDF.FIRST, executionURI, appURI );
			connectionFactory.getConnection().add( newQueueMemberBNode, RDF.REST, RDF.NIL, appURI );
			connectionFactory.getConnection().add( lastQueueMemberSubject, RDF.REST, newQueueMemberBNode, appURI );
			connectionFactory.getConnection().remove( lastQueueMemberSubject, RDF.REST, RDF.NIL, appURI );

		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	private static final String getAppRelatedQuery;

	static {
		getAppRelatedQuery = "" +
			"SELECT ?app" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?executionsContainerURI {" + NEW_LINE +
			TAB + TAB + "?executionsContainerURI <" + ContainerDescription.Property.MEMBERSHIP_RESOURCE.getURI().stringValue() + "> ?job" + NEW_LINE +
			TAB + "}." + NEW_LINE +
			TAB + "GRAPH ?job {" + NEW_LINE +
			TAB + TAB + "?job <" + JobDescription.Property.APP_RELATED.getURI().stringValue() + "> ?app" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}";
	}

	@Override
	public URI getAppRelatedURI( URI executionsContainerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "executionsContainerURI", executionsContainerURI );

		return sparqlTemplate.executeTupleQuery( getAppRelatedQuery, bindings, queryResult -> {
			if ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "app" );
				if ( ValueUtil.isURI( member ) ) return ValueUtil.getURI( member );
			}
			throw new RuntimeException( "there is not an app related" );
		} );
	}

	@Override
	public void addResult( URI executionURI, Value status ) {
		try {
			connectionFactory.getConnection().add( executionURI, ExecutionDescription.Property.RESULT.getURI(), status, executionURI );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public void addErrorDescription( URI executionURI, String error ) {
		try {
			connectionFactory.getConnection().add( executionURI, ExecutionDescription.Property.ERROR_DESCRIPTION.getURI(), ValueFactoryImpl.getInstance().createLiteral( error ), executionURI );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	private Resource getLastMemberBNode( URI appURI ) {
		RepositoryResult<Statement> statements;
		try {
			statements = connectionFactory.getConnection().getStatements( null, RDF.REST, RDF.NIL, false, appURI );
			if ( ! statements.hasNext() ) throw new RuntimeException( "queue is malformed" );
			return statements.next().getSubject();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}
}
