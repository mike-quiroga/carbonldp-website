package com.carbonldp.ldp.nonrdf;

import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;

public interface NonRDFSourceService {

	@PreAuthorize( "hasPermission(#rdfRepresentation, 'DOWNLOAD')" )
	public File getResource( RDFRepresentation rdfRepresentation );

	public boolean isRDFRepresentation( IRI targetIRI );

	@PreAuthorize( "hasPermission(#rdfRepresentation, 'UPDATE')" )
	public void replace( RDFRepresentation rdfRepresentation, File requestEntity, String contentType );
}
