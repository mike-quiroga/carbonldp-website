package com.carbonldp.ldp.nonrdf;

import org.openrdf.model.URI;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface NonRDFSourceRepository {

	public Set<String> getFileIdentifiers( URI rdfRepresentationURI );
}
