package com.carbonldp.rdf;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public interface RDFBlankNodeRepository extends RDFNodeRepository<BNode> {

	public BNode get( String identifier, IRI documentIRI );
}
