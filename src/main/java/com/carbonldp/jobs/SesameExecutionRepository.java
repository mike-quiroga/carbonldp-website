package com.carbonldp.jobs;

import com.carbonldp.jobs.ExecutionDescription.Status;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.carbonldp.Consts.*;

/**
 * @author NestorVenegas
 * @since 0.33.0
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
		BNode queueFirstElement = executionQueueLocation.getBNode( ExecutionDescription.List.QUEUE.getIRI() );
		if ( queueFirstElement == null ) resourceRepository.add( executionQueueLocationIRI, ExecutionDescription.List.QUEUE.getIRI(), bNode );
		else enqueueElement( bNode, queueFirstElement, executionQueueLocationIRI );
	}

	private static final String dequeueQuery;

	static {
		dequeueQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?context <" + ExecutionDescription.List.QUEUE.getIRI() + "> ?queueFirstElement." + NEW_LINE +
			TAB + TAB + "?queueFirstElement ?p ?o." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}" + NEW_LINE +
			"INSERT {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?context <" + ExecutionDescription.List.QUEUE.getIRI() + "> ?nextElement ." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queueFirstElement <" + RDF.REST + "> ?nextElement ." + NEW_LINE +
			TAB + TAB + "?queueFirstElement ?p ?o." + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}";
	}

	@Override
	public void dequeue( IRI executionQueueLocationIRI ) {
		Resource queueFirstElement = resourceRepository.getResource( executionQueueLocationIRI, ExecutionDescription.List.QUEUE.getIRI() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "queueFirstElement", queueFirstElement );
		bindings.put( "context", executionQueueLocationIRI );

		sparqlTemplate.executeUpdate( dequeueQuery, bindings );
		if ( resourceRepository.getResource( executionQueueLocationIRI, ExecutionDescription.List.QUEUE.getIRI() ).equals( RDF.NIL ) ) resourceRepository.remove( executionQueueLocationIRI, ExecutionDescription.List.QUEUE.getIRI() );
	}

	private static final String peekQuery;

	static {
		peekQuery = "" +
			"SELECT ?item" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?context {" + NEW_LINE +
			TAB + TAB + "?queue <" + RDF.FIRST + "> ?item ; " + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}";
	}

	@Override
	public Execution peek( IRI executionQueueLocationIRI ) {
		BNode executionQueue = resourceRepository.getBNode( executionQueueLocationIRI, ExecutionDescription.List.QUEUE.getIRI() );
		if ( executionQueue == null ) return null;

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

	private void enqueueElement( BNode bNode, BNode queueFirstElement, IRI executionQueueLocationIRI ) {

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "bnode", bNode );
		bindings.put( "queue", queueFirstElement );
		bindings.put( "context", executionQueueLocationIRI );

		sparqlTemplate.executeUpdate( enqueueQuery, bindings );
	}

	@Override
	public void addResult( IRI executionIRI, Value status ) {
		connectionTemplate.write( connection -> connection.add( executionIRI, ExecutionDescription.Property.RESULT.getIRI(), status, executionIRI ) );
	}

	@Override
	public void addErrorDescription( IRI executionIRI, String error ) {
		connectionTemplate.write( connection -> connection.add( executionIRI, ExecutionDescription.Property.ERROR_DESCRIPTION.getIRI(), SimpleValueFactory.getInstance().createLiteral( error ), executionIRI ) );
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
