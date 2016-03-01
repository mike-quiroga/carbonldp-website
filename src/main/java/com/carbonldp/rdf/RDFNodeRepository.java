package com.carbonldp.rdf;

import org.joda.time.DateTime;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public interface RDFNodeRepository<T extends Resource> {

	public boolean hasProperty( T subject, URI pred, URI documentURI );

	public boolean hasProperty( T subject, RDFNodeEnum pred, URI documentURI );

	public boolean contains( T subject, URI pred, Value obj, URI documentURI );

	public boolean contains( T subject, RDFNodeEnum pred, Value obj, URI documentURI );

	public boolean contains( T subject, RDFNodeEnum pred, RDFNodeEnum obj, URI documentURI );

	public Value getProperty( T subject, URI pred, URI documentURI );

	public Value getProperty( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Value> getProperties( T subject, URI pred, URI documentURI );

	public Set<Value> getProperties( T subject, RDFNodeEnum pred, URI documentURI );

	public URI getURI( T subject, URI pred, URI documentURI );

	public URI getURI( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<URI> getURIs( T subject, URI pred, URI documentURI );

	public Set<URI> getURIs( T subject, RDFNodeEnum pred, URI documentURI );

	public Boolean getBoolean( T subject, URI pred, URI documentURI );

	public Boolean getBoolean( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Boolean> getBooleans( T subject, URI pred, URI documentURI );

	public Set<Boolean> getBooleans( T subject, RDFNodeEnum pred, URI documentURI );

	public Byte getByte( T subject, URI pred, URI documentURI );

	public Byte getByte( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Byte> getBytes( T subject, URI pred, URI documentURI );

	public Set<Byte> getBytes( T subject, RDFNodeEnum pred, URI documentURI );

	public DateTime getDate( T subject, URI pred, URI documentURI );

	public DateTime getDate( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<DateTime> getDates( T subject, URI pred, URI documentURI );

	public Set<DateTime> getDates( T subject, RDFNodeEnum pred, URI documentURI );

	public Double getDouble( T subject, URI pred, URI documentURI );

	public Double getDouble( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Double> getDoubles( T subject, URI pred, URI documentURI );

	public Set<Double> getDoubles( T subject, RDFNodeEnum pred, URI documentURI );

	public Float getFloat( T subject, URI pred, URI documentURI );

	public Float getFloat( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Float> getFloats( T subject, URI pred, URI documentURI );

	public Set<Float> getFloats( T subject, RDFNodeEnum pred, URI documentURI );

	public Integer getInteger( T subject, URI pred, URI documentURI );

	public Integer getInteger( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Integer> getIntegers( T subject, URI pred, URI documentURI );

	public Set<Integer> getIntegers( T subject, RDFNodeEnum pred, URI documentURI );

	public Long getLong( T subject, URI pred, URI documentURI );

	public Long getLong( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Long> getLongs( T subject, URI pred, URI documentURI );

	public Set<Long> getLongs( T subject, RDFNodeEnum pred, URI documentURI );

	public Short getShort( T subject, URI pred, URI documentURI );

	public Short getShort( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<Short> getShorts( T subject, URI pred, URI documentURI );

	public Set<Short> getShorts( T subject, RDFNodeEnum pred, URI documentURI );

	public String getString( T subject, URI pred, URI documentURI );

	public String getString( T subject, RDFNodeEnum pred, URI documentURI );

	public String getString( T subject, URI pred, URI documentURI, Set<String> languages );

	public String getString( T subject, RDFNodeEnum pred, URI documentURI, Set<String> languages );

	public Set<String> getStrings( T subject, URI pred, URI documentURI );

	public Set<String> getStrings( T subject, RDFNodeEnum pred, URI documentURI );

	public Set<String> getStrings( T subject, URI pred, URI documentURI, Set<String> languages );

	public Set<String> getStrings( T subject, RDFNodeEnum pred, URI documentURI, Set<String> languages );

	public void add( T subject, URI pred, Value obj, URI documentURI );

	public void add( T subject, URI predicate, Collection<Value> values, URI documentURI );

	public void add( T subject, URI pred, boolean obj, URI documentURI );

	public void add( T subject, URI pred, byte obj, URI documentURI );

	public void add( T subject, URI pred, DateTime obj, URI documentURI );

	public void add( T subject, URI pred, double obj, URI documentURI );

	public void add( T subject, URI pred, float obj, URI documentURI );

	public void add( T subject, URI pred, int obj, URI documentURI );

	public void add( T subject, URI pred, long obj, URI documentURI );

	public void add( T subject, URI pred, short obj, URI documentURI );

	public void add( T subject, URI pred, String obj, URI documentURI );

	public void add( T subject, URI pred, String obj, URI documentURI, String language );

	public void remove( T subject, URI pred, URI documentURI );

	public void remove( T subject, RDFNodeEnum pred, URI documentURI );

	public void remove( T subject, URI pred, Value obj, URI documentURI );

	public void remove( T subject, URI predicate, Set<Value> values, URI documentURI );

	public void remove( T subject, URI pred, boolean obj, URI documentURI );

	public void remove( T subject, URI pred, byte obj, URI documentURI );

	public void remove( T subject, URI pred, DateTime obj, URI documentURI );

	public void remove( T subject, URI pred, double obj, URI documentURI );

	public void remove( T subject, URI pred, float obj, URI documentURI );

	public void remove( T subject, URI pred, int obj, URI documentURI );

	public void remove( T subject, URI pred, long obj, URI documentURI );

	public void remove( T subject, URI pred, short obj, URI documentURI );

	public void remove( T subject, URI pred, String obj, URI documentURI );

	public void remove( T subject, URI pred, String obj, URI documentURI, String language );

	public void set( T subject, URI pred, Value obj, URI documentURI );

	public void set( T subject, URI pred, boolean obj, URI documentURI );

	public void set( T subject, URI pred, byte obj, URI documentURI );

	public void set( T subject, URI pred, DateTime obj, URI documentURI );

	public void set( T subject, URI pred, double obj, URI documentURI );

	public void set( T subject, URI pred, float obj, URI documentURI );

	public void set( T subject, URI pred, int obj, URI documentURI );

	public void set( T subject, URI pred, long obj, URI documentURI );

	public void set( T subject, URI pred, short obj, URI documentURI );

	public void set( T subject, URI pred, String obj, URI documentURI );

	public void set( T subject, URI pred, String obj, URI documentURI, String language );

	public boolean hasType( T subject, URI type, URI documentURI );

	public boolean hasType( T subject, RDFNodeEnum type, URI documentURI );

	public Set<URI> getTypes( T subject, URI documentURI );

	public void addType( T subject, URI type, URI documentURI );

	public void removeType( T subject, URI type, URI documentURI );

	public void setType( T subject, URI type, URI documentURI );

}
