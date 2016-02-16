package com.carbonldp.ldp.nonrdf;

import org.openrdf.model.URI;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.27.5-ALPHA
 */
public interface NonRDFSourceRepository {

	public Set<String> getFileIdentifiers( URI rdfRepresentationURI );

	public void delete( URI rdfRepresentationURI );
}
