package com.carbonldp.ldp.nonrdf;

import org.openrdf.model.URI;

public interface RDFRepresentationRepository {

	public void create( URI containerURI, RDFRepresentation rdfRepresentation );
}
