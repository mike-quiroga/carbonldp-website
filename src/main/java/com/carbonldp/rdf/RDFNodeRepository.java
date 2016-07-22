package com.carbonldp.rdf;

import org.joda.time.DateTime;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

import java.util.Collection;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public interface RDFNodeRepository<T extends Resource> {

	public boolean hasProperty( T subject, IRI pred, IRI documentIRI );

	public boolean hasProperty( T subject, RDFNodeEnum pred, IRI documentIRI );

	public boolean contains( T subject, IRI pred, Value obj, IRI documentIRI );

	public boolean contains( T subject, RDFNodeEnum pred, Value obj, IRI documentIRI );

	public boolean contains( T subject, RDFNodeEnum pred, RDFNodeEnum obj, IRI documentIRI );

	public Value getProperty( T subject, IRI pred, IRI documentIRI );

	public Value getProperty( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Value> getProperties( T subject, IRI pred, IRI documentIRI );

	public Set<Value> getProperties( T subject, RDFNodeEnum pred, IRI documentIRI );

	public IRI getIRI( T subject, IRI pred, IRI documentIRI );

	public IRI getIRI( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<IRI> getIRIs( T subject, IRI pred, IRI documentIRI );

	public Set<IRI> getIRIs( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Boolean getBoolean( T subject, IRI pred, IRI documentIRI );

	public Boolean getBoolean( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Boolean> getBooleans( T subject, IRI pred, IRI documentIRI );

	public Set<Boolean> getBooleans( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Byte getByte( T subject, IRI pred, IRI documentIRI );

	public Byte getByte( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Byte> getBytes( T subject, IRI pred, IRI documentIRI );

	public Set<Byte> getBytes( T subject, RDFNodeEnum pred, IRI documentIRI );

	public DateTime getDate( T subject, IRI pred, IRI documentIRI );

	public DateTime getDate( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<DateTime> getDates( T subject, IRI pred, IRI documentIRI );

	public Set<DateTime> getDates( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Double getDouble( T subject, IRI pred, IRI documentIRI );

	public Double getDouble( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Double> getDoubles( T subject, IRI pred, IRI documentIRI );

	public Set<Double> getDoubles( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Float getFloat( T subject, IRI pred, IRI documentIRI );

	public Float getFloat( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Float> getFloats( T subject, IRI pred, IRI documentIRI );

	public Set<Float> getFloats( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Integer getInteger( T subject, IRI pred, IRI documentIRI );

	public Integer getInteger( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Integer> getIntegers( T subject, IRI pred, IRI documentIRI );

	public Set<Integer> getIntegers( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Long getLong( T subject, IRI pred, IRI documentIRI );

	public Long getLong( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Long> getLongs( T subject, IRI pred, IRI documentIRI );

	public Set<Long> getLongs( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Short getShort( T subject, IRI pred, IRI documentIRI );

	public Short getShort( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<Short> getShorts( T subject, IRI pred, IRI documentIRI );

	public Set<Short> getShorts( T subject, RDFNodeEnum pred, IRI documentIRI );

	public String getString( T subject, IRI pred, IRI documentIRI );

	public String getString( T subject, RDFNodeEnum pred, IRI documentIRI );

	public String getString( T subject, IRI pred, IRI documentIRI, Set<String> languages );

	public String getString( T subject, RDFNodeEnum pred, IRI documentIRI, Set<String> languages );

	public Set<String> getStrings( T subject, IRI pred, IRI documentIRI );

	public Set<String> getStrings( T subject, RDFNodeEnum pred, IRI documentIRI );

	public Set<String> getStrings( T subject, IRI pred, IRI documentIRI, Set<String> languages );

	public Set<String> getStrings( T subject, RDFNodeEnum pred, IRI documentIRI, Set<String> languages );

	public void add( T subject, IRI pred, Value obj, IRI documentIRI );

	public void add( T subject, IRI predicate, Collection<Value> values, IRI documentIRI );

	public void add( T subject, IRI pred, boolean obj, IRI documentIRI );

	public void add( T subject, IRI pred, byte obj, IRI documentIRI );

	public void add( T subject, IRI pred, DateTime obj, IRI documentIRI );

	public void add( T subject, IRI pred, double obj, IRI documentIRI );

	public void add( T subject, IRI pred, float obj, IRI documentIRI );

	public void add( T subject, IRI pred, int obj, IRI documentIRI );

	public void add( T subject, IRI pred, long obj, IRI documentIRI );

	public void add( T subject, IRI pred, short obj, IRI documentIRI );

	public void add( T subject, IRI pred, String obj, IRI documentIRI );

	public void add( T subject, IRI pred, String obj, IRI documentIRI, String language );

	public void remove( T subject, IRI pred, IRI documentIRI );

	public void remove( T subject, RDFNodeEnum pred, IRI documentIRI );

	public void remove( T subject, IRI pred, Value obj, IRI documentIRI );

	public void remove( T subject, IRI predicate, Set<Value> values, IRI documentIRI );

	public void remove( T subject, IRI pred, boolean obj, IRI documentIRI );

	public void remove( T subject, IRI pred, byte obj, IRI documentIRI );

	public void remove( T subject, IRI pred, DateTime obj, IRI documentIRI );

	public void remove( T subject, IRI pred, double obj, IRI documentIRI );

	public void remove( T subject, IRI pred, float obj, IRI documentIRI );

	public void remove( T subject, IRI pred, int obj, IRI documentIRI );

	public void remove( T subject, IRI pred, long obj, IRI documentIRI );

	public void remove( T subject, IRI pred, short obj, IRI documentIRI );

	public void remove( T subject, IRI pred, String obj, IRI documentIRI );

	public void remove( T subject, IRI pred, String obj, IRI documentIRI, String language );

	public void set( T subject, IRI pred, Value obj, IRI documentIRI );

	public void set( T subject, IRI pred, boolean obj, IRI documentIRI );

	public void set( T subject, IRI pred, byte obj, IRI documentIRI );

	public void set( T subject, IRI pred, DateTime obj, IRI documentIRI );

	public void set( T subject, IRI pred, double obj, IRI documentIRI );

	public void set( T subject, IRI pred, float obj, IRI documentIRI );

	public void set( T subject, IRI pred, int obj, IRI documentIRI );

	public void set( T subject, IRI pred, long obj, IRI documentIRI );

	public void set( T subject, IRI pred, short obj, IRI documentIRI );

	public void set( T subject, IRI pred, String obj, IRI documentIRI );

	public void set( T subject, IRI pred, String obj, IRI documentIRI, String language );

	public boolean hasType( T subject, IRI type, IRI documentIRI );

	public boolean hasType( T subject, RDFNodeEnum type, IRI documentIRI );

	public Set<IRI> getTypes( T subject, IRI documentIRI );

	public void addType( T subject, IRI type, IRI documentIRI );

	public void removeType( T subject, IRI type, IRI documentIRI );

	public void setType( T subject, IRI type, IRI documentIRI );

}
