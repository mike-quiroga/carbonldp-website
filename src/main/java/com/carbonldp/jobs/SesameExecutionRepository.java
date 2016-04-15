package com.carbonldp.jobs;

import com.carbonldp.jobs.ExecutionDescription.Status;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.*;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
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
	public void changeExecutionStatus( IRI executionIRI, Status status ) {
		connectionTemplate.write( connection -> {
			connection.remove( executionIRI, ExecutionDescription.Property.STATUS.getIRI(), null, executionIRI );
			connection.add( executionIRI, ExecutionDescription.Property.STATUS.getIRI(), status.getIRI(), executionIRI );
		} );
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
	public void enqueue( BNode bNode, IRI executionQueueLocationIRI ) {
		RDFSource executionQueueLocation = sourceRepository.get( executionQueueLocationIRI );
		IRI executionQueue = executionQueueLocation.getIRI( ExecutionDescription.List.QUEUE.getIRI() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "bnode", bNode );
		bindings.put( "queue", executionQueue );
		bindings.put( "context", executionQueueLocationIRI );

		sparqlTemplate.executeUpdate( enqueueQuery, bindings );

	}

	private static final String dequeueQuery;

	static {
		dequeueQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queue <" + RDF.REST + "> ?dequeueElement." + NEW_LINE +
			TAB + TAB + "?dequeueElement ?p ?o." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}" + NEW_LINE +
			"INSERT {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queue <" + RDF.REST + "> ?nextElement ." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queue <" + RDF.REST + "> ?dequeueElement ." + NEW_LINE +
			TAB + TAB + "?dequeueElement <" + RDF.REST + "> ?nextElement . " + NEW_LINE +
			TAB + TAB + "?dequeueElement ?p ?o." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}";
	}

	@Override
	public void dequeue( IRI executionQueueLocationIRI ) {
		RDFSource executionQueueLocation = sourceRepository.get( executionQueueLocationIRI );
		IRI executionQueue = executionQueueLocation.getIRI( ExecutionDescription.List.QUEUE.getIRI() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "queue", executionQueue );
		bindings.put( "context", executionQueueLocationIRI );

		sparqlTemplate.executeUpdate( dequeueQuery, bindings );

	}

	private static final String peekQuery;

	static {
		peekQuery = "" +
			"SELECT ?item" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queue <" + RDF.REST + "> ?queueElement ." + NEW_LINE +
			TAB + TAB + "?queueElement <" + RDF.FIRST + "> ?item ; " + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}";
	}

	@Override
	public Execution peek( IRI executionQueueLocationIRI ) {
		RDFSource executionQueueLocation = sourceRepository.get( executionQueueLocationIRI );
		IRI executionQueue = executionQueueLocation.getIRI( ExecutionDescription.List.QUEUE.getIRI() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "queue", executionQueue );
		bindings.put( "context", executionQueueLocationIRI );

		IRI executionIRI = sparqlTemplate.executeTupleQuery( peekQuery, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return null;
			BindingSet bindingSet = queryResult.next();
			Value executionValue = bindingSet.getValue( "item" );
			if ( ValueUtil.isIRI( executionValue ) ) return ValueUtil.getIRI( executionValue );
			throw new IllegalStateException( "malformed query" );

		} );
		if ( executionIRI == null ) return null;
		return new Execution( sourceRepository.get( executionIRI ) );
	}

	@Override
	public void addResult( IRI executionIRI, Value status ) {
		connectionTemplate.write( connection -> connection.add( executionIRI, ExecutionDescription.Property.RESULT.getIRI(), status, executionIRI ) );
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
