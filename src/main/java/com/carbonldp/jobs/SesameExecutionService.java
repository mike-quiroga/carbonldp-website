package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.ConnectionRWTemplate;
import org.eclipse.jetty.server.ConnectionFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameExecutionService extends AbstractSesameLDPService implements ExecutionService {
	private ExecutionRepository executionRepository;
	private RDFSourceService sourceService;
	private ConnectionRWTemplate connectionTemplate;

	@Override
	public void changeExecutionStatus( IRI executionIRI, ExecutionDescription.Status status ) {
		executionRepository.changeExecutionStatus( executionIRI, status );
		sourceRepository.touch( executionIRI );
	}

	@Override
	public void enqueue( IRI executionIRI, IRI executionQueueLocationIRI ) {
		RDFDocument document = new RDFDocument( new LinkedHashModel(), executionQueueLocationIRI );
		BNode bNode = connectionTemplate.read( connection -> connection.getValueFactory().createBNode() );
		RDFBlankNode blankNode = new RDFBlankNode( document, bNode, executionQueueLocationIRI );
		blankNode.add( RDF.FIRST, executionIRI );
		blankNode.add( RDF.REST, RDF.NIL );

		sourceService.add( executionQueueLocationIRI, document );
		executionRepository.enqueue( bNode, executionQueueLocationIRI );
		sourceRepository.touch( executionQueueLocationIRI );
	}

	@Override
	public void dequeue( IRI executionQueueLocationIRI ) {
		executionRepository.dequeue( executionQueueLocationIRI );
		sourceRepository.touch( executionQueueLocationIRI );
	}

	@Override
	public Execution peek( IRI executionQueueLocationIRI ) {
		return executionRepository.peek( executionQueueLocationIRI );
	}

	@Override
	public void addResult( IRI executionIRI, Value status ) {
		executionRepository.addResult( executionIRI, status );
		sourceRepository.touch( executionIRI );
	}

	@Override
	public void addErrorDescription( IRI executionIRI, String error ){
		executionRepository.addErrorDescription( executionIRI, error );
		sourceRepository.touch( executionIRI );
	}

	@Autowired
	public void setExecutionRepository( ExecutionRepository executionRepository ) {
		this.executionRepository = executionRepository;
	}

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}

	@Autowired
	public void setConnectionTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}
}
