package com.carbonldp.ldp.nonrdf;

import org.openrdf.model.IRI;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.27.5-ALPHA
 */
public interface NonRDFSourceRepository {

	public Set<String> getFileIdentifiers( IRI rdfRepresentationIRI );

	public void delete( IRI rdfRepresentationIRI );
}
