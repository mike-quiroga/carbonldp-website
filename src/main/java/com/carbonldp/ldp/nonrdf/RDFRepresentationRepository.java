package com.carbonldp.ldp.nonrdf;

import java.io.File;

public interface RDFRepresentationRepository {
	public void create( RDFRepresentation rdfRepresentation, File file, String mimeType );
}
