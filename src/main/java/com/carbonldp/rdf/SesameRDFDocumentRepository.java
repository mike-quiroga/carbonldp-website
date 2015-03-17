package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFDocumentUtil;
import info.aduna.iteration.Iterations;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// TODO: LDP-331
@Transactional
public class SesameRDFDocumentRepository extends AbstractSesameRepository implements RDFDocumentRepository {

	public SesameRDFDocumentRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	public boolean documentExists( URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, documentURI ),
			RepositoryResult::hasNext
		);
	}

	public RDFDocument getDocument( URI documentURI ) {
		AbstractModel model = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, documentURI ),
			this::retrieveModel
		);
		return new RDFDocument( model, documentURI );
	}

	public Set<RDFDocument> getDocuments( Collection<? extends URI> documentURIs ) {
		URI[] contexts = documentURIs.toArray( new URI[documentURIs.size()] );
		AbstractModel model = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, contexts ),
			this::retrieveModel
		);
		return RDFDocumentUtil.getDocuments( model, documentURIs );
	}

	public void addDocument( RDFDocument document ) {
		connectionTemplate.write( connection -> connection.add( document ) );
	}

	public void addDocuments( Collection<RDFDocument> documents ) {
		documents.forEach( this::addDocument );
	}

	@Override
	public void update( RDFDocument document ) {
		connectionTemplate.write( connection -> {
			connection.remove( document.subjectSelector(), document.predicateSelector(), document.objectSelector(), document.contextSelector() );
			addDocument( document );
		} );
	}

	public void deleteDocument( URI documentURI ) {
		// Remove ambiguity
		Resource subject = null;
		connectionTemplate.write( connection -> connection.remove( subject, null, null, documentURI ) );
	}

	public void deleteDocuments( Collection<URI> documentURIs ) {
		URI[] contexts = documentURIs.toArray( new URI[documentURIs.size()] );
		// Remove ambiguity
		Resource subject = null;
		connectionTemplate.write( connection -> connection.remove( subject, null, null, contexts ) );
	}

	private AbstractModel retrieveModel( RepositoryResult<Statement> statementsIterator ) throws RepositoryException {
		Set<Statement> statements = new HashSet<>();
		Iterations.addAll( statementsIterator, statements );
		return new LinkedHashModel( statements );
	}
}
