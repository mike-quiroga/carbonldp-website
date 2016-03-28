package com.carbonldp.jobs;

import com.carbonldp.jobs.ExecutionDescription.Status;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import org.openrdf.model.*;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private RDFSourceRepository sourceRepository;

	public SesameExecutionRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public Status getExecutionStatus( URI executionURI ) {
		Statement statement = connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( executionURI, ExecutionDescription.Property.STATUS.getURI(), null, false, executionURI );
			if ( ! statements.hasNext() ) throw new RuntimeException( "execution does not have a status" );
			return statements.next();
		} );

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
		connectionTemplate.write( connection -> connection.remove( executionURI, ExecutionDescription.Property.STATUS.getURI(), null, executionURI ) );
		connectionTemplate.write( connection -> connection.add( executionURI, ExecutionDescription.Property.STATUS.getURI(), status.getURI(), executionURI ) );
	}

	private static final String enqueueQuery;

	static {
		enqueueQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + " ?insertionPoint <" + RDF.REST + "> <" + RDF.NIL + ">." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}" + NEW_LINE +
			"INSERT {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?bnode <" + RDF.FIRST + "> ?execution ;" + NEW_LINE +
			TAB + TAB + TAB + "<" + RDF.REST + "> <" + RDF.NIL + ">." + NEW_LINE +
			TAB + TAB + "?insertionPoint <" + RDF.REST + "> ?bnode." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queue <" + RDF.REST + ">*/<" + RDF.FIRST + "> ?item ." + NEW_LINE +
			TAB + TAB + "?insertionPoint <" + RDF.FIRST + "> ?item ; " + NEW_LINE +
			TAB + TAB + TAB + "<" + RDF.REST + "> ?rest . " + NEW_LINE +
			TAB + TAB + "FILTER(?rest = <" + RDF.NIL + ">)" + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}";

	}

	@Override
	public void enqueue( URI executionURI, URI executionQueueLocationURI ) {

		RDFSource executionQueueLocation = sourceRepository.get( executionQueueLocationURI );
		URI executionQueue = executionQueueLocation.getURI( ExecutionDescription.List.QUEUE.getURI() );
		BNode newQueueMemberBNode = connectionTemplate.read( connection -> connection.getValueFactory().createBNode() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "bnode", newQueueMemberBNode );
		bindings.put( "execution", executionURI );
		bindings.put( "queue", executionQueue );
		bindings.put( "context", executionQueueLocationURI );

		sparqlTemplate.executeUpdate( enqueueQuery, bindings );

	}

	@Override
	public void addResult( URI executionURI, Value status ) {
		connectionTemplate.write( connection -> connection.add( executionURI, ExecutionDescription.Property.RESULT.getURI(), status, executionURI ) );
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
