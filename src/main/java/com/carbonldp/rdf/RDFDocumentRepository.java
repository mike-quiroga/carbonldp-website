package com.carbonldp.rdf;

import org.openrdf.model.IRI;

import java.util.Collection;
import java.util.Set;

public interface RDFDocumentRepository {
	public boolean documentExists( IRI documentIRI );

	public RDFDocument getDocument( IRI documentIRI );

	public Set<RDFDocument> getDocuments( Collection<? extends IRI> documentIRIs );

	public void addDocument( RDFDocument document );

	public void addDocuments( Collection<RDFDocument> documents );

	public void update( RDFDocument document );

	public void deleteDocument( IRI documentIRI );

	public void deleteDocuments( Collection<IRI> documentIRIs );

	public void add( IRI sourceIRI, RDFDocument document );

	public void subtract( IRI sourceIRI, RDFDocument document );

	public void set( IRI sourceIRI, RDFDocument document );

}
