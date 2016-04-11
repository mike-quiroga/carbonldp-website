package com.carbonldp.rdf;

import org.joda.time.DateTime;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Set;

public interface RDFResourceRepository extends RDFNodeRepository<IRI> {
	public boolean hasProperty( IRI resourceIRI, IRI pred );

	public boolean hasProperty( IRI resourceIRI, RDFNodeEnum pred );

	public boolean contains( IRI resourceIRI, IRI pred, Value obj );

	public boolean contains( IRI resourceIRI, RDFNodeEnum pred, Value obj );

	public boolean contains( IRI resourceIRI, RDFNodeEnum pred, RDFNodeEnum obj );

	public Value getProperty( IRI resourceIRI, IRI pred );

	public Value getProperty( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Value> getProperties( IRI resourceIRI, IRI pred );

	public Set<Value> getProperties( IRI resourceIRI, RDFNodeEnum pred );

	public IRI getIRI( IRI resourceIRI, IRI pred );

	public IRI getIRI( IRI resourceIRI, RDFNodeEnum pred );

	public Set<IRI> getIRIs( IRI resourceIRI, IRI pred );

	public Set<IRI> getIRIs( IRI resourceIRI, RDFNodeEnum pred );

	public Boolean getBoolean( IRI resourceIRI, IRI pred );

	public Boolean getBoolean( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Boolean> getBooleans( IRI resourceIRI, IRI pred );

	public Set<Boolean> getBooleans( IRI resourceIRI, RDFNodeEnum pred );

	public Byte getByte( IRI resourceIRI, IRI pred );

	public Byte getByte( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Byte> getBytes( IRI resourceIRI, IRI pred );

	public Set<Byte> getBytes( IRI resourceIRI, RDFNodeEnum pred );

	public DateTime getDate( IRI resourceIRI, IRI pred );

	public DateTime getDate( IRI resourceIRI, RDFNodeEnum pred );

	public Set<DateTime> getDates( IRI resourceIRI, IRI pred );

	public Set<DateTime> getDates( IRI resourceIRI, RDFNodeEnum pred );

	public Double getDouble( IRI resourceIRI, IRI pred );

	public Double getDouble( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Double> getDoubles( IRI resourceIRI, IRI pred );

	public Set<Double> getDoubles( IRI resourceIRI, RDFNodeEnum pred );

	public Float getFloat( IRI resourceIRI, IRI pred );

	public Float getFloat( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Float> getFloats( IRI resourceIRI, IRI pred );

	public Set<Float> getFloats( IRI resourceIRI, RDFNodeEnum pred );

	public Integer getInteger( IRI resourceIRI, IRI pred );

	public Integer getInteger( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Integer> getIntegers( IRI resourceIRI, IRI pred );

	public Set<Integer> getIntegers( IRI resourceIRI, RDFNodeEnum pred );

	public Long getLong( IRI resourceIRI, IRI pred );

	public Long getLong( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Long> getLongs( IRI resourceIRI, IRI pred );

	public Set<Long> getLongs( IRI resourceIRI, RDFNodeEnum pred );

	public Short getShort( IRI resourceIRI, IRI pred );

	public Short getShort( IRI resourceIRI, RDFNodeEnum pred );

	public Set<Short> getShorts( IRI resourceIRI, IRI pred );

	public Set<Short> getShorts( IRI resourceIRI, RDFNodeEnum pred );

	public String getString( IRI resourceIRI, IRI pred );

	public String getString( IRI resourceIRI, RDFNodeEnum pred );

	public String getString( IRI resourceIRI, IRI pred, Set<String> languages );

	public String getString( IRI resourceIRI, RDFNodeEnum pred, Set<String> languages );

	public Set<String> getStrings( IRI resourceIRI, IRI pred );

	public Set<String> getStrings( IRI resourceIRI, RDFNodeEnum pred );

	public Set<String> getStrings( IRI resourceIRI, IRI pred, Set<String> languages );

	public Set<String> getStrings( IRI resourceIRI, RDFNodeEnum pred, Set<String> languages );

	public void add( IRI resourceIRI, IRI pred, Value obj );

	public void add( IRI resourceViewIRI, IRI predicate, Collection<Value> values );

	public void add( IRI resourceIRI, IRI pred, boolean obj );

	public void add( IRI resourceIRI, IRI pred, byte obj );

	public void add( IRI resourceIRI, IRI pred, DateTime obj );

	public void add( IRI resourceIRI, IRI pred, double obj );

	public void add( IRI resourceIRI, IRI pred, float obj );

	public void add( IRI resourceIRI, IRI pred, int obj );

	public void add( IRI resourceIRI, IRI pred, long obj );

	public void add( IRI resourceIRI, IRI pred, short obj );

	public void add( IRI resourceIRI, IRI pred, String obj );

	public void add( IRI resourceIRI, IRI pred, String obj, String language );

	public void remove( IRI resourceIRI, IRI pred );

	public void remove( IRI resourceIRI, RDFNodeEnum pred );

	public void remove( IRI resourceIRI, IRI pred, Value obj );

	public void remove( IRI resourceViewIRI, IRI predicate, Set<Value> values );

	public void remove( IRI resourceIRI, IRI pred, boolean obj );

	public void remove( IRI resourceIRI, IRI pred, byte obj );

	public void remove( IRI resourceIRI, IRI pred, DateTime obj );

	public void remove( IRI resourceIRI, IRI pred, double obj );

	public void remove( IRI resourceIRI, IRI pred, float obj );

	public void remove( IRI resourceIRI, IRI pred, int obj );

	public void remove( IRI resourceIRI, IRI pred, long obj );

	public void remove( IRI resourceIRI, IRI pred, short obj );

	public void remove( IRI resourceIRI, IRI pred, String obj );

	public void remove( IRI resourceIRI, IRI pred, String obj, String language );

	public void set( IRI resourceIRI, IRI pred, Value obj );

	public void set( IRI resourceIRI, IRI pred, boolean obj );

	public void set( IRI resourceIRI, IRI pred, byte obj );

	public void set( IRI resourceIRI, IRI pred, DateTime obj );

	public void set( IRI resourceIRI, IRI pred, double obj );

	public void set( IRI resourceIRI, IRI pred, float obj );

	public void set( IRI resourceIRI, IRI pred, int obj );

	public void set( IRI resourceIRI, IRI pred, long obj );

	public void set( IRI resourceIRI, IRI pred, short obj );

	public void set( IRI resourceIRI, IRI pred, String obj );

	public void set( IRI resourceIRI, IRI pred, String obj, String language );

	public boolean hasType( IRI resourceIRI, IRI type );

	public boolean hasType( IRI resourceIRI, RDFNodeEnum type );

	public Set<IRI> getTypes( IRI resourceIRI );

	public void addType( IRI resourceIRI, IRI type );

	public void removeType( IRI resourceIRI, IRI type );

	public void setType( IRI resourceIRI, IRI type );
}
