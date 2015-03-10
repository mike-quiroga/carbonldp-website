package com.carbonldp.rdf;

import com.carbonldp.descriptions.RDFNodeEnum;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Set;

public interface RDFResourceRepository {
	public boolean hasProperty(URI resourceURI, URI pred);

	public boolean hasProperty(URI resourceURI, RDFNodeEnum pred);

	public boolean contains(URI resourceURI, URI pred, Value obj);

	public boolean contains(URI resourceURI, RDFNodeEnum pred, Value obj);

	public boolean contains(URI resourceURI, RDFNodeEnum pred, RDFNodeEnum obj);

	public Value getProperty(URI resourceURI, URI pred);

	public Value getProperty(URI resourceURI, RDFNodeEnum pred);

	public Set<Value> getProperties(URI resourceURI, URI pred);

	public Set<Value> getProperties(URI resourceURI, RDFNodeEnum pred);

	public URI getURI(URI resourceURI, URI pred);

	public URI getURI(URI resourceURI, RDFNodeEnum pred);

	public Set<URI> getURIs(URI resourceURI, URI pred);

	public Set<URI> getURIs(URI resourceURI, RDFNodeEnum pred);

	public Boolean getBoolean(URI resourceURI, URI pred);

	public Boolean getBoolean(URI resourceURI, RDFNodeEnum pred);

	public Set<Boolean> getBooleans(URI resourceURI, URI pred);

	public Set<Boolean> getBooleans(URI resourceURI, RDFNodeEnum pred);

	public Byte getByte(URI resourceURI, URI pred);

	public Byte getByte(URI resourceURI, RDFNodeEnum pred);

	public Set<Byte> getBytes(URI resourceURI, URI pred);

	public Set<Byte> getBytes(URI resourceURI, RDFNodeEnum pred);

	public DateTime getDate(URI resourceURI, URI pred);

	public DateTime getDate(URI resourceURI, RDFNodeEnum pred);

	public Set<DateTime> getDates(URI resourceURI, URI pred);

	public Set<DateTime> getDates(URI resourceURI, RDFNodeEnum pred);

	public Double getDouble(URI resourceURI, URI pred);

	public Double getDouble(URI resourceURI, RDFNodeEnum pred);

	public Set<Double> getDoubles(URI resourceURI, URI pred);

	public Set<Double> getDoubles(URI resourceURI, RDFNodeEnum pred);

	public Float getFloat(URI resourceURI, URI pred);

	public Float getFloat(URI resourceURI, RDFNodeEnum pred);

	public Set<Float> getFloats(URI resourceURI, URI pred);

	public Set<Float> getFloats(URI resourceURI, RDFNodeEnum pred);

	public Integer getInteger(URI resourceURI, URI pred);

	public Integer getInteger(URI resourceURI, RDFNodeEnum pred);

	public Set<Integer> getIntegers(URI resourceURI, URI pred);

	public Set<Integer> getIntegers(URI resourceURI, RDFNodeEnum pred);

	public Long getLong(URI resourceURI, URI pred);

	public Long getLong(URI resourceURI, RDFNodeEnum pred);

	public Set<Long> getLongs(URI resourceURI, URI pred);

	public Set<Long> getLongs(URI resourceURI, RDFNodeEnum pred);

	public Short getShort(URI resourceURI, URI pred);

	public Short getShort(URI resourceURI, RDFNodeEnum pred);

	public Set<Short> getShorts(URI resourceURI, URI pred);

	public Set<Short> getShorts(URI resourceURI, RDFNodeEnum pred);

	public String getString(URI resourceURI, URI pred);

	public String getString(URI resourceURI, RDFNodeEnum pred);

	public String getString(URI resourceURI, URI pred, Set<String> languages);

	public String getString(URI resourceURI, RDFNodeEnum pred, Set<String> languages);

	public Set<String> getStrings(URI resourceURI, URI pred);

	public Set<String> getStrings(URI resourceURI, RDFNodeEnum pred);

	public Set<String> getStrings(URI resourceURI, URI pred, Set<String> languages);

	public Set<String> getStrings(URI resourceURI, RDFNodeEnum pred, Set<String> languages);

	public void add(URI resourceURI, URI pred, Value obj);

	public void add(URI resourceURI, URI pred, boolean obj);

	public void add(URI resourceURI, URI pred, byte obj);

	public void add(URI resourceURI, URI pred, DateTime obj);

	public void add(URI resourceURI, URI pred, double obj);

	public void add(URI resourceURI, URI pred, float obj);

	public void add(URI resourceURI, URI pred, int obj);

	public void add(URI resourceURI, URI pred, long obj);

	public void add(URI resourceURI, URI pred, short obj);

	public void add(URI resourceURI, URI pred, String obj);

	public void add(URI resourceURI, URI pred, String obj, String language);

	public void remove(URI resourceURI, URI pred);

	public void remove(URI resourceURI, RDFNodeEnum pred);

	public void remove(URI resourceURI, URI pred, Value obj);

	public void remove(URI resourceURI, URI pred, boolean obj);

	public void remove(URI resourceURI, URI pred, byte obj);

	public void remove(URI resourceURI, URI pred, DateTime obj);

	public void remove(URI resourceURI, URI pred, double obj);

	public void remove(URI resourceURI, URI pred, float obj);

	public void remove(URI resourceURI, URI pred, int obj);

	public void remove(URI resourceURI, URI pred, long obj);

	public void remove(URI resourceURI, URI pred, short obj);

	public void remove(URI resourceURI, URI pred, String obj);

	public void remove(URI resourceURI, URI pred, String obj, String language);

	public void set(URI resourceURI, URI pred, Value obj);

	public void set(URI resourceURI, URI pred, boolean obj);

	public void set(URI resourceURI, URI pred, byte obj);

	public void set(URI resourceURI, URI pred, DateTime obj);

	public void set(URI resourceURI, URI pred, double obj);

	public void set(URI resourceURI, URI pred, float obj);

	public void set(URI resourceURI, URI pred, int obj);

	public void set(URI resourceURI, URI pred, long obj);

	public void set(URI resourceURI, URI pred, short obj);

	public void set(URI resourceURI, URI pred, String obj);

	public void set(URI resourceURI, URI pred, String obj, String language);

	public boolean hasType(URI resourceURI, URI type);

	public boolean hasType(URI resourceURI, RDFNodeEnum type);

	public Set<URI> getTypes(URI resourceURI);

	public void addType(URI resourceURI, URI type);

	public void removeType(URI resourceURI, URI type);

	public void setType(URI resourceURI, URI type);

}
