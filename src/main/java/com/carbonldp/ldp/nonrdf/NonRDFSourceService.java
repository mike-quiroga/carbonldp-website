package com.carbonldp.ldp.nonrdf;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;

public interface NonRDFSourceService {

	@PreAuthorize( "hasPermission(#rdfRepresentation, 'DOWNLOAD')" )
	public File getResource( RDFRepresentation rdfRepresentation );

	public boolean isRDFRepresentation( URI targetURI );

	@PreAuthorize( "hasPermission(#rdfRepresentation, 'UPDATE')" )
	public void replace( RDFRepresentation rdfRepresentation, File requestEntity, String contentType );
}
