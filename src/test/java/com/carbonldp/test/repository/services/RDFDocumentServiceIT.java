package com.carbonldp.test.repository.services;

import static org.testng.Assert.assertTrue;

import org.openrdf.model.impl.URIImpl;
import org.testng.annotations.Test;

import com.carbonldp.test.AbstractIT;

public class RDFDocumentServiceIT extends AbstractIT {
	@Test
	public void addDocument() {

	}

	@Test
	public void addDocuments() {

	}

	@Test
	public void deleteDocument() {

	}

	@Test
	public void deleteDocuments() {

	}

	@Test
	public void documentExists() {
		repositoryIDProvider.setRepositoryID(testRepositoryID);
		assertTrue(rdfDocumentService.documentExists(new URIImpl(testResourceURI)));
	}

	@Test
	public void getDocument() {

	}

	@Test
	public void getDocuments() {

	}
}
