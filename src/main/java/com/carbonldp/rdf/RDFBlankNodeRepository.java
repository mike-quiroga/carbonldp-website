package com.carbonldp.rdf;

import org.joda.time.DateTime;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public interface RDFBlankNodeRepository extends RDFNodeRepository<BNode> {

	public BNode get( String identifier, URI documentURI );
}
