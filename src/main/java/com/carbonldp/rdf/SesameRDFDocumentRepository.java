package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFDocumentUtil;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
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

	public boolean documentExists( IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, documentIRI ),
			repositoryResult -> repositoryResult.hasNext()
		);
	}

	public RDFDocument getDocument( IRI documentIRI ) {
		AbstractModel model = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, documentIRI ),
			repositoryResult -> retrieveModel( repositoryResult )
		);
		return new RDFDocument( model, documentIRI );
	}

	public Set<RDFDocument> getDocuments( Collection<? extends IRI> documentIRIs ) {
		IRI[] contexts = documentIRIs.toArray( new IRI[documentIRIs.size()] );
		AbstractModel model = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, contexts ),
			repositoryResult -> retrieveModel( repositoryResult )
		);
		return RDFDocumentUtil.getDocuments( model, documentIRIs );
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

	public void deleteDocument( IRI documentIRI ) {
		// Remove ambiguity
		Resource subject = null;
		connectionTemplate.write( connection -> connection.remove( subject, null, null, documentIRI ) );
	}

	public void deleteDocuments( Collection<IRI> documentIRIs ) {
		IRI[] contexts = documentIRIs.toArray( new IRI[documentIRIs.size()] );
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
	public void add( IRI sourceIRI, RDFDocument document ) {
		Collection<RDFResource> resourceViews = document.getFragmentResources();
		resourceViews.add( document.getDocumentResource() );
		Collection<RDFBlankNode> blankNodes = document.getBlankNodes();
		IRI documentIRI = document.getDocumentResource().getDocumentIRI();

		for ( RDFResource resourceView : resourceViews ) {
			IRI resourceViewIRI = resourceView.getIRI();
			Map<IRI, Set<Value>> propertiesMap = resourceView.getPropertiesMap();
			for ( IRI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				resourceRepository.add( resourceViewIRI, predicate, values, documentIRI );
			}
		}

		for ( RDFBlankNode blankNode : blankNodes ) {
			BNode blankNodeSubject = blankNode.getSubject();
			Map<IRI, Set<Value>> propertiesMap = blankNode.getPropertiesMap();
			for ( IRI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				blankNodeRepository.add( blankNodeSubject, predicate, values, documentIRI );
			}
		}
	}

	@Override
	public void set( IRI sourceIRI, RDFDocument document ) {
		Collection<RDFResource> resourceViews = document.getFragmentResources();
		resourceViews.add( document.getDocumentResource() );
		Collection<RDFBlankNode> blankNodes = document.getBlankNodes();
		IRI documentIRI = document.getDocumentResource().getDocumentIRI();

		for ( RDFResource resourceView : resourceViews ) {
			IRI resourceViewIRI = resourceView.getIRI();
			Map<IRI, Set<Value>> propertiesMap = resourceView.getPropertiesMap();
			for ( IRI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				resourceRepository.remove( resourceViewIRI, predicate );
				resourceRepository.add( resourceViewIRI, predicate, values );
			}
		}

		for ( RDFBlankNode blankNode : blankNodes ) {
			BNode blankNodeSubject = blankNode.getSubject();
			Map<IRI, Set<Value>> propertiesMap = blankNode.getPropertiesMap();
			for ( IRI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				blankNodeRepository.remove( blankNodeSubject, predicate, documentIRI );
				blankNodeRepository.add( blankNodeSubject, predicate, values, documentIRI );
			}
		}

	}

	@Override
	public void subtract( IRI sourceIRI, RDFDocument document ) {
		Collection<RDFResource> resourceViews = document.getFragmentResources();
		resourceViews.add( document.getDocumentResource() );
		Collection<RDFBlankNode> blankNodes = document.getBlankNodes();
		IRI documentIRI = document.getDocumentResource().getDocumentIRI();

		for ( RDFResource resourceView : resourceViews ) {
			IRI resourceViewIRI = resourceView.getIRI();
			Map<IRI, Set<Value>> propertiesMap = resourceView.getPropertiesMap();
			for ( IRI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				resourceRepository.remove( resourceViewIRI, predicate, values );
			}
		}

		for ( RDFBlankNode blankNode : blankNodes ) {
			BNode blankNodeSubject = blankNode.getSubject();
			Map<IRI, Set<Value>> propertiesMap = blankNode.getPropertiesMap();
			for ( IRI predicate : propertiesMap.keySet() ) {
				Set<Value> values = propertiesMap.get( predicate );
				blankNodeRepository.remove( blankNodeSubject, predicate, values, documentIRI );
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
