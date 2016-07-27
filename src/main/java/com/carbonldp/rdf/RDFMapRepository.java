package com.carbonldp.rdf;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface RDFMapRepository {

	public Set<RDFBlankNode> getEntries( IRI mapIRI );

	public RDFBlankNode getEntry( IRI mapIRI, Value key );

	public Set<Value> getKeys( IRI mapIRI );

	public boolean hasKey( IRI mapIRI, Value key );

	public Value getValue( IRI mapIRI, Value key );

	public Set<Value> getValues( IRI mapIRI, Value key );

	public void add( IRI mapIRI, Value key, Value... values );

	public void remove( IRI mapIRI, Value key, Value... values );

	public void remove( IRI mapIRI, Value key );
}
