package com.carbonldp.test.rdf;

import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.test.AbstractIT;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

// TODO: LDP-331 - Finish it after issue has been fixed
@Test( enabled = false )
public class RDFDocumentRepositoryIT extends AbstractIT {

	@Autowired
	RDFDocumentRepository documentRepository;

	IRI documentIRI = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/" );

	@Test
	public void documentExistTest() {
		IRI subj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );

		assertEquals( documentRepository.documentExists( documentIRI ), true );
		assertEquals( documentRepository.documentExists( subj ), false );
	}

	@Test
	public void getDocumentTest() {
		RDFDocument document = documentRepository.getDocument( documentIRI );
		assertEquals( Models.subjectIRI( document ).orElse( null ), documentIRI );
	}

	@Test
	public void getDocumentsTest() {
		Set<IRI> documentIRIs = new HashSet<>();
		documentIRIs.add( documentIRI );
		Set<RDFDocument> documents = documentRepository.getDocuments( documentIRIs );
		Iterator iterator = documents.iterator();
		assertNotNull( iterator );
		assertEquals( iterator.next(), documentIRI );

	}

	@Test
	public void addAndDeleteDocumentTest() {
		Model testModel = new LinkedHashModel();
		IRI subj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#is" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#post" );
		testModel.add( subj, pred, obj, subj );
		RDFDocument document = new RDFDocument( (AbstractModel) testModel, subj );
		documentRepository.addDocument( document );
		assertEquals( documentRepository.documentExists( subj ), true );
		documentRepository.deleteDocument( subj );
		assertEquals( documentRepository.documentExists( subj ), false );

	}

	@Test
	public void addAndDeleteDocumentsTest() {
		Model testModel = new LinkedHashModel();
		IRI subj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#is" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#post" );
		testModel.add( subj, pred, obj, subj );
		RDFDocument document = new RDFDocument( (AbstractModel) testModel, subj );
		Collection<RDFDocument> documents = new HashSet<>();
		documents.add( document );

		documentRepository.addDocuments( documents );
		assertEquals( documentRepository.documentExists( subj ), true );

		Collection<IRI> subjects = new HashSet<>();
		subjects.add( subj );
		documentRepository.deleteDocuments( subjects );
		assertEquals( documentRepository.documentExists( subj ), false );

	}

	@Test
	public void updateTest() {
		Model testModel = new LinkedHashModel();
		IRI subj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#is" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#post" );
		testModel.add( subj, pred, obj, subj );
		RDFDocument document = new RDFDocument( (AbstractModel) testModel, subj );
		documentRepository.update( document );
		assertEquals( documentRepository.documentExists( subj ), true );

	}
}
