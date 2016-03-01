package com.carbonldp.rdf;

import com.carbonldp.utils.URIUtil;
import org.joda.time.DateTime;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Transactional
public class SesameRDFResourceRepository extends SesameRDFNodeRepository<URI> implements RDFResourceRepository {

	public SesameRDFResourceRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public boolean hasProperty( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.hasProperty( resourceURI, pred, documentURI );
	}

	@Override
	public boolean hasProperty( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.hasProperty( resourceURI, pred, documentURI );
	}

	@Override
	public boolean contains( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.contains( resourceURI, pred, obj, documentURI );
	}

	@Override
	public boolean contains( URI resourceURI, RDFNodeEnum pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.contains( resourceURI, pred, obj, documentURI );
	}

	@Override
	public boolean contains( URI resourceURI, RDFNodeEnum pred, RDFNodeEnum obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.contains( resourceURI, pred, obj, documentURI );
	}

	@Override
	public Value getProperty( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getProperty( resourceURI, pred, documentURI );
	}

	@Override
	public Value getProperty( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getProperty( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Value> getProperties( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getProperties( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Value> getProperties( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getProperties( resourceURI, pred, documentURI );
	}

	@Override
	public URI getURI( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getURI( resourceURI, pred, documentURI );
	}

	@Override
	public URI getURI( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getURI( resourceURI, pred, documentURI );
	}

	@Override
	public Set<URI> getURIs( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getURIs( resourceURI, pred, documentURI );
	}

	@Override
	public Set<URI> getURIs( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getURIs( resourceURI, pred, documentURI );
	}

	@Override
	public Boolean getBoolean( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getBoolean( resourceURI, pred, documentURI );
	}

	@Override
	public Boolean getBoolean( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getBoolean( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Boolean> getBooleans( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getBooleans( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Boolean> getBooleans( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getBooleans( resourceURI, pred, documentURI );
	}

	@Override
	public Byte getByte( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getByte( resourceURI, pred, documentURI );
	}

	@Override
	public Byte getByte( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getByte( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Byte> getBytes( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getBytes( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Byte> getBytes( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getBytes( resourceURI, pred, documentURI );
	}

	@Override
	public DateTime getDate( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDate( resourceURI, pred, documentURI );
	}

	@Override
	public DateTime getDate( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDate( resourceURI, pred, documentURI );
	}

	@Override
	public Set<DateTime> getDates( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDates( resourceURI, pred, documentURI );
	}

	@Override
	public Set<DateTime> getDates( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDates( resourceURI, pred, documentURI );
	}

	@Override
	public Double getDouble( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDouble( resourceURI, pred, documentURI );
	}

	@Override
	public Double getDouble( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDouble( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Double> getDoubles( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDoubles( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Double> getDoubles( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getDoubles( resourceURI, pred, documentURI );
	}

	@Override
	public Float getFloat( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getFloat( resourceURI, pred, documentURI );
	}

	@Override
	public Float getFloat( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getFloat( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Float> getFloats( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getFloats( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Float> getFloats( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getFloats( resourceURI, pred, documentURI );
	}

	@Override
	public Integer getInteger( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getInteger( resourceURI, pred, documentURI );
	}

	@Override
	public Integer getInteger( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getInteger( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Integer> getIntegers( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getIntegers( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Integer> getIntegers( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getIntegers( resourceURI, pred, documentURI );
	}

	@Override
	public Long getLong( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getLong( resourceURI, pred, documentURI );
	}

	@Override
	public Long getLong( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getLong( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Long> getLongs( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getLongs( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Long> getLongs( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getLongs( resourceURI, pred, documentURI );
	}

	@Override
	public Short getShort( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getShort( resourceURI, pred, documentURI );
	}

	@Override
	public Short getShort( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getShort( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Short> getShorts( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getShorts( resourceURI, pred, documentURI );
	}

	@Override
	public Set<Short> getShorts( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getShorts( resourceURI, pred, documentURI );
	}

	@Override
	public String getString( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getString( resourceURI, pred, documentURI );
	}

	@Override
	public String getString( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getString( resourceURI, pred, documentURI );
	}

	@Override
	public Set<String> getStrings( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getStrings( resourceURI, pred, documentURI );
	}

	@Override
	public Set<String> getStrings( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getStrings( resourceURI, pred, documentURI );
	}

	@Override
	public String getString( URI resourceURI, URI pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getString( resourceURI, pred, documentURI, languages );
	}

	@Override
	public String getString( URI resourceURI, RDFNodeEnum pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getString( resourceURI, pred, documentURI, languages );
	}

	@Override
	public Set<String> getStrings( URI resourceURI, URI pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getStrings( resourceURI, pred, documentURI, languages );
	}

	@Override
	public Set<String> getStrings( URI resourceURI, RDFNodeEnum pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getStrings( resourceURI, pred, documentURI, languages );
	}

	@Override
	public void add( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI predicate, Collection<Value> values ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, predicate, values, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, boolean obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, byte obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, DateTime obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, double obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, float obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, int obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, long obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, short obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, String obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void add( URI resourceURI, URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, documentURI );
	}

	@Override
	public void remove( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI predicate, Set<Value> values ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, predicate, values, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, boolean obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, byte obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, DateTime obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, double obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, float obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, int obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, long obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, short obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, String obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void remove( URI resourceURI, URI pred, String obj, String language ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.remove( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, boolean obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, byte obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, DateTime obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, double obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, float obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, int obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, long obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, short obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, String obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public void set( URI resourceURI, URI pred, String obj, String language ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.set( resourceURI, pred, obj, documentURI );
	}

	@Override
	public boolean hasType( URI resourceURI, URI type ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.hasType( resourceURI, type, documentURI );
	}

	@Override
	public boolean hasType( URI resourceURI, RDFNodeEnum type ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.hasType( resourceURI, type, documentURI );
	}

	@Override
	public Set<URI> getTypes( URI resourceURI ) {
		URI documentURI = getDocumentURI( resourceURI );
		return super.getURIs( resourceURI, RDFResourceDescription.Property.TYPE, documentURI );
	}

	@Override
	public void addType( URI resourceURI, URI type ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.add( resourceURI, RDFResourceDescription.Property.TYPE.getURI(), type, documentURI );
	}

	@Override
	public void removeType( URI resourceURI, URI type ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.removeType( resourceURI, type, documentURI );
	}

	@Override
	public void setType( URI resourceURI, URI type ) {
		URI documentURI = getDocumentURI( resourceURI );
		super.setType( resourceURI, type, documentURI );
	}

	private URI getDocumentURI( URI resourceURI ) {
		if ( ! URIUtil.hasFragment( resourceURI ) ) return resourceURI;
		return new URIImpl( URIUtil.getDocumentURI( resourceURI.stringValue() ) );
	}

}
