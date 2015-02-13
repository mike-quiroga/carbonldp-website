package com.carbonldp.repository;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;

import com.carbonldp.models.RDFDocument;

public interface RDFDocumentRepository {
	public boolean documentExists(URI documentURI);

	public RDFDocument getDocument(URI documentURI);

	public Set<RDFDocument> getDocuments(Collection<? extends URI> documentURIs);

	public void addDocument(RDFDocument document);

	public void addDocuments(Collection<RDFDocument> documents);

	public void deleteDocument(URI documentURI);

	public void deleteDocuments(Collection<URI> documentURIs);
}
