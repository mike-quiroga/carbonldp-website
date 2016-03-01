package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFDocumentUtil;
import info.aduna.iteration.Iterations;
import org.openrdf.model.*;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO: LDP-331
@Transactional
public class SesameRDFDocumentRepository extends AbstractSesameRepository implements RDFDocumentRepository {
	protected RDFBlankNodeRepository blankNodeRepository;
	protected RDFResourceRepository resourceRepository;

	public SesameRDFDocumentRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	public boolean documentExists( URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, documentURI ),
			repositoryResult -> repositoryResult.hasNext()
		);
	}

	public RDFDocument getDocument( URI documentURI ) {
		AbstractModel model = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, documentURI ),
			repositoryResult -> retrieveModel( repositoryResult )
		);
		return new RDFDocument( model, documentURI );
	}

	public Set<RDFDocument> getDocuments( Collection<? extends URI> documentURIs ) {
		URI[] contexts = documentURIs.toArray( new URI[documentURIs.size()] );
		AbstractModel model = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, contexts ),
			repositoryResult -> retrieveModel( repositoryResult )
		);
		return RDFDocumentUtil.getDocuments( model, documentURIs );
	}

	public void addDocument( RDFDocument document ) {
		RDFDocumentFactory.getInstance().addMissingIdentifiersToBlankNodes( document );
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

	@Override
	public void add( URI sourceURI, RDFDocument document ) {
		Collection<RDFResource> resourceViews = document.getFragmentResources();
		resourceViews.add( document.getDocumentResource() );
		Collection<RDFBlankNode> blankNodes = document.getBlankNodes();
		URI documentURI = document.getDocumentResource().getDocumentURI();

		for ( RDFResource resourceView : resourceViews ) {
			URI resourceViewURI = resourceView.getURI();
			Map<URI, Set<Value>> propertiesMap = resourceView.getPropertiesMap();
			for ( URI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				resourceRepository.add( resourceViewURI, predicate, values, documentURI );
			}
		}

		for ( RDFBlankNode blankNode : blankNodes ) {
			BNode blankNodeSubject = blankNode.getSubject();
			Map<URI, Set<Value>> propertiesMap = blankNode.getPropertiesMap();
			for ( URI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				blankNodeRepository.add( blankNodeSubject, predicate, values, documentURI );
			}
		}
	}

	@Override
	public void set( URI sourceURI, RDFDocument document ) {
		Collection<RDFResource> resourceViews = document.getFragmentResources();
		resourceViews.add( document.getDocumentResource() );
		Collection<RDFBlankNode> blankNodes = document.getBlankNodes();
		URI documentURI = document.getDocumentResource().getDocumentURI();

		for ( RDFResource resourceView : resourceViews ) {
			URI resourceViewURI = resourceView.getURI();
			Map<URI, Set<Value>> propertiesMap = resourceView.getPropertiesMap();
			for ( URI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				resourceRepository.remove( resourceViewURI, predicate );
				resourceRepository.add( resourceViewURI, predicate, values );
			}
		}

		for ( RDFBlankNode blankNode : blankNodes ) {
			BNode blankNodeSubject = blankNode.getSubject();
			Map<URI, Set<Value>> propertiesMap = blankNode.getPropertiesMap();
			for ( URI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				blankNodeRepository.remove( blankNodeSubject, predicate, documentURI );
				blankNodeRepository.add( blankNodeSubject, predicate, values, documentURI );
			}
		}

	}

	@Override
	public void subtract( URI sourceURI, RDFDocument document ) {
		Collection<RDFResource> resourceViews = document.getFragmentResources();
		resourceViews.add( document.getDocumentResource() );
		Collection<RDFBlankNode> blankNodes = document.getBlankNodes();
		URI documentURI = document.getDocumentResource().getDocumentURI();

		for ( RDFResource resourceView : resourceViews ) {
			URI resourceViewURI = resourceView.getURI();
			Map<URI, Set<Value>> propertiesMap = resourceView.getPropertiesMap();
			for ( URI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				resourceRepository.remove( resourceViewURI, predicate, values );
			}
		}

		for ( RDFBlankNode blankNode : blankNodes ) {
			BNode blankNodeSubject = blankNode.getSubject();
			Map<URI, Set<Value>> propertiesMap = blankNode.getPropertiesMap();
			for ( URI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				blankNodeRepository.remove( blankNodeSubject, predicate, values, documentURI );
			}
		}

	}

	@Autowired
	public void setBlankNodeRepository( RDFBlankNodeRepository blankNodeRepository ) {
		this.blankNodeRepository = blankNodeRepository;
	}

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) {
		this.resourceRepository = resourceRepository;
	}
}
