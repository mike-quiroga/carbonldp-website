package com.carbonldp.rdf;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public interface RDFBlankNodeRepository extends RDFNodeRepository<BNode> {

	public BNode get( String identifier, IRI documentIRI );
}
