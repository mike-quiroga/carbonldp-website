package com.carbonldp.ldp.nonrdf;

import org.openrdf.model.URI;

import java.io.File;

public interface NonRDFSourceService {

	public File getResource( RDFRepresentation rdfRepresentation );

	public boolean isRDFRepresentation( URI targetURI );

	public void deleteResource( RDFRepresentation rdfRepresentation );

	public void replace( RDFRepresentation rdfRepresentation, File requestEntity );
}
