package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.ConnectionRWTemplate;
import org.eclipse.jetty.server.ConnectionFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
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
	public ExecutionDescription.Status getExecutionStatus( URI executionURI ) {
		return executionRepository.getExecutionStatus( executionURI );
	}

	@Override
	public void changeExecutionStatus( URI executionURI, ExecutionDescription.Status status ) {
		executionRepository.changeExecutionStatus( executionURI, status );
		sourceRepository.touch( executionURI );
	}

	@Override
	public void enqueue( URI executionURI, URI executionQueueLocationURI ) {
		RDFDocument document = new RDFDocument( new LinkedHashModel(), executionQueueLocationURI );
		BNode bNode = connectionTemplate.read( connection -> connection.getValueFactory().createBNode() );
		RDFBlankNode blankNode = new RDFBlankNode( document, bNode, executionQueueLocationURI );
		blankNode.add( RDF.FIRST, executionURI );
		blankNode.add( RDF.REST, RDF.NIL );

		sourceService.add( executionQueueLocationURI, document );
		executionRepository.enqueue( bNode, executionQueueLocationURI );
		sourceRepository.touch( executionQueueLocationURI );
	}

	@Override
	public void dequeue( URI executionQueueLocationURI ) {
		executionRepository.dequeue( executionQueueLocationURI );
		sourceRepository.touch( executionQueueLocationURI );
	}

	@Override
	public Execution peek( URI executionQueueLocationURI ) {
		return executionRepository.peek( executionQueueLocationURI );
	}

	@Override
	public void addResult( URI executionURI, Value status ) {
		executionRepository.addResult( executionURI, status );
		sourceRepository.touch( executionURI );
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
