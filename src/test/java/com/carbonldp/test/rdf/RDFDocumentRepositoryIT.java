package com.carbonldp.test.rdf;

import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

// TODO: LDP-331 - Finish it after issue has been fixed
@Test( enabled = false )
public class RDFDocumentRepositoryIT extends AbstractIT {

	@Autowired
	RDFDocumentRepository documentRepository;

	URI documentURI = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/" );

	@Test
	public void documentExistTest() {
		URI subj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );

		assertEquals( documentRepository.documentExists( documentURI ), true );
		assertEquals( documentRepository.documentExists( subj ), false );
	}

	@Test
	public void getDocumentTest() {
		RDFDocument document = documentRepository.getDocument( documentURI );
		assertEquals( document.subjectURI(), documentURI );
	}

	@Test
	public void getDocumentsTest() {
		Set<URI> documentURIs = new HashSet<>();
		documentURIs.add( documentURI );
		Set<RDFDocument> documents = documentRepository.getDocuments( documentURIs );
		Iterator iterator = documents.iterator();
		assertNotNull( iterator );
		assertEquals( iterator.next(), documentURI );

	}

	@Test
	public void addAndDeleteDocumentTest() {
		Model testModel = new LinkedHashModel();
		URI subj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );
		URI pred = new URIImpl( "http://example.org/ns#is" );
		URI obj = new URIImpl( "http://example.org/ns#post" );
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
		URI subj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );
		URI pred = new URIImpl( "http://example.org/ns#is" );
		URI obj = new URIImpl( "http://example.org/ns#post" );
		testModel.add( subj, pred, obj, subj );
		RDFDocument document = new RDFDocument( (AbstractModel) testModel, subj );
		Collection<RDFDocument> documents = new HashSet<>();
		documents.add( document );

		documentRepository.addDocuments( documents );
		assertEquals( documentRepository.documentExists( subj ), true );

		Collection<URI> subjects = new HashSet<>();
		subjects.add( subj );
		documentRepository.deleteDocuments( subjects );
		assertEquals( documentRepository.documentExists( subj ), false );

	}

	@Test
	public void updateTest() {
		Model testModel = new LinkedHashModel();
		URI subj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-42" );
		URI pred = new URIImpl( "http://example.org/ns#is" );
		URI obj = new URIImpl( "http://example.org/ns#post" );
		testModel.add( subj, pred, obj, subj );
		RDFDocument document = new RDFDocument( (AbstractModel) testModel, subj );
		documentRepository.update( document );
		assertEquals( documentRepository.documentExists( subj ), true );

	}
}
