package com.carbonldp.rdf;

import org.joda.time.DateTime;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface RDFBlankNodeRepository {

	public BNode get( String identifier, URI documentURI );

	public boolean hasProperty( BNode blankNode, URI pred, URI documentURI );

	public boolean hasProperty( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public boolean contains( BNode blankNode, URI pred, Value obj, URI documentURI );

	public boolean contains( BNode blankNode, RDFNodeEnum pred, Value obj, URI documentURI );

	public boolean contains( BNode blankNode, RDFNodeEnum pred, RDFNodeEnum obj, URI documentURI );

	public Value getProperty( BNode blankNode, URI pred, URI documentURI );

	public Value getProperty( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Value> getProperties( BNode blankNode, URI pred, URI documentURI );

	public Set<Value> getProperties( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public URI getURI( BNode blankNode, URI pred, URI documentURI );

	public URI getURI( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<URI> getURIs( BNode blankNode, URI pred, URI documentURI );

	public Set<URI> getURIs( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Boolean getBoolean( BNode blankNode, URI pred, URI documentURI );

	public Boolean getBoolean( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Boolean> getBooleans( BNode blankNode, URI pred, URI documentURI );

	public Set<Boolean> getBooleans( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Byte getByte( BNode blankNode, URI pred, URI documentURI );

	public Byte getByte( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Byte> getBytes( BNode blankNode, URI pred, URI documentURI );

	public Set<Byte> getBytes( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public DateTime getDate( BNode blankNode, URI pred, URI documentURI );

	public DateTime getDate( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<DateTime> getDates( BNode blankNode, URI pred, URI documentURI );

	public Set<DateTime> getDates( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Double getDouble( BNode blankNode, URI pred, URI documentURI );

	public Double getDouble( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Double> getDoubles( BNode blankNode, URI pred, URI documentURI );

	public Set<Double> getDoubles( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Float getFloat( BNode blankNode, URI pred, URI documentURI );

	public Float getFloat( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Float> getFloats( BNode blankNode, URI pred, URI documentURI );

	public Set<Float> getFloats( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Integer getInteger( BNode blankNode, URI pred, URI documentURI );

	public Integer getInteger( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Integer> getIntegers( BNode blankNode, URI pred, URI documentURI );

	public Set<Integer> getIntegers( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Long getLong( BNode blankNode, URI pred, URI documentURI );

	public Long getLong( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Long> getLongs( BNode blankNode, URI pred, URI documentURI );

	public Set<Long> getLongs( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Short getShort( BNode blankNode, URI pred, URI documentURI );

	public Short getShort( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<Short> getShorts( BNode blankNode, URI pred, URI documentURI );

	public Set<Short> getShorts( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public String getString( BNode blankNode, URI pred, URI documentURI );

	public String getString( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public String getString( BNode blankNode, URI pred, URI documentURI, Set<String> languages );

	public String getString( BNode blankNode, RDFNodeEnum pred, URI documentURI, Set<String> languages );

	public Set<String> getStrings( BNode blankNode, URI pred, URI documentURI );

	public Set<String> getStrings( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public Set<String> getStrings( BNode blankNode, URI pred, URI documentURI, Set<String> languages );

	public Set<String> getStrings( BNode blankNode, RDFNodeEnum pred, URI documentURI, Set<String> languages );

	public void add( BNode blankNode, URI pred, Value obj, URI documentURI );

	public void add( BNode blankNode, URI predicate, Collection<Value> values, URI documentURI );

	public void add( BNode blankNode, URI pred, boolean obj, URI documentURI );

	public void add( BNode blankNode, URI pred, byte obj, URI documentURI );

	public void add( BNode blankNode, URI pred, DateTime obj, URI documentURI );

	public void add( BNode blankNode, URI pred, double obj, URI documentURI );

	public void add( BNode blankNode, URI pred, float obj, URI documentURI );

	public void add( BNode blankNode, URI pred, int obj, URI documentURI );

	public void add( BNode blankNode, URI pred, long obj, URI documentURI );

	public void add( BNode blankNode, URI pred, short obj, URI documentURI );

	public void add( BNode blankNode, URI pred, String obj, URI documentURI );

	public void add( BNode blankNode, URI pred, String obj, URI documentURI, String language );

	public void remove( BNode blankNode, URI pred, URI documentURI );

	public void remove( BNode blankNode, RDFNodeEnum pred, URI documentURI );

	public void remove( BNode blankNode, URI pred, Value obj, URI documentURI );

	public void remove( BNode blankNode, URI predicate, Set<Value> values, URI documentURI );

	public void remove( BNode blankNode, URI pred, boolean obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, byte obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, DateTime obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, double obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, float obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, int obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, long obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, short obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, String obj, URI documentURI );

	public void remove( BNode blankNode, URI pred, String obj, URI documentURI, String language );

	public void set( BNode blankNode, URI pred, Value obj, URI documentURI );

	public void set( BNode blankNode, URI pred, boolean obj, URI documentURI );

	public void set( BNode blankNode, URI pred, byte obj, URI documentURI );

	public void set( BNode blankNode, URI pred, DateTime obj, URI documentURI );

	public void set( BNode blankNode, URI pred, double obj, URI documentURI );

	public void set( BNode blankNode, URI pred, float obj, URI documentURI );

	public void set( BNode blankNode, URI pred, int obj, URI documentURI );

	public void set( BNode blankNode, URI pred, long obj, URI documentURI );

	public void set( BNode blankNode, URI pred, short obj, URI documentURI );

	public void set( BNode blankNode, URI pred, String obj, URI documentURI );

	public void set( BNode blankNode, URI pred, String obj, URI documentURI, String language );

}
