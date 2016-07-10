package com.carbonldp.rdf;

import com.carbonldp.utils.IRIUtil;
import org.joda.time.DateTime;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Transactional
public class SesameRDFResourceRepository extends SesameRDFNodeRepository<IRI> implements RDFResourceRepository {

	public SesameRDFResourceRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public boolean hasProperty( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.hasProperty( resourceIRI, pred, documentIRI );
	}

	@Override
	public boolean hasProperty( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.hasProperty( resourceIRI, pred, documentIRI );
	}

	@Override
	public boolean contains( IRI resourceIRI, IRI pred, Value obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.contains( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public boolean contains( IRI resourceIRI, RDFNodeEnum pred, Value obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.contains( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public boolean contains( IRI resourceIRI, RDFNodeEnum pred, RDFNodeEnum obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.contains( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public Value getProperty( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getProperty( resourceIRI, pred, documentIRI );
	}

	@Override
	public Value getProperty( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getProperty( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Value> getProperties( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getProperties( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Value> getProperties( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getProperties( resourceIRI, pred, documentIRI );
	}

	@Override
	public IRI getIRI( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIRI( resourceIRI, pred, documentIRI );
	}

	@Override
	public IRI getIRI( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIRI( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<IRI> getIRIs( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIRIs( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<IRI> getIRIs( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIRIs( resourceIRI, pred, documentIRI );
	}

	@Override
	public BNode getBNode( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBNode( resourceIRI, pred, documentIRI );
	}

	@Override
	public BNode getBNode( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBNode( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<BNode> getBNodes( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBNodes( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<BNode> getBNodes( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBNodes( resourceIRI, pred, documentIRI );
	}

	@Override
	public Boolean getBoolean( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBoolean( resourceIRI, pred, documentIRI );
	}

	@Override
	public Boolean getBoolean( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBoolean( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Boolean> getBooleans( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBooleans( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Boolean> getBooleans( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBooleans( resourceIRI, pred, documentIRI );
	}

	@Override
	public Byte getByte( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getByte( resourceIRI, pred, documentIRI );
	}

	@Override
	public Byte getByte( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getByte( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Byte> getBytes( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBytes( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Byte> getBytes( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getBytes( resourceIRI, pred, documentIRI );
	}

	@Override
	public DateTime getDate( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDate( resourceIRI, pred, documentIRI );
	}

	@Override
	public DateTime getDate( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDate( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<DateTime> getDates( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDates( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<DateTime> getDates( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDates( resourceIRI, pred, documentIRI );
	}

	@Override
	public Double getDouble( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDouble( resourceIRI, pred, documentIRI );
	}

	@Override
	public Double getDouble( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDouble( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Double> getDoubles( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDoubles( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Double> getDoubles( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getDoubles( resourceIRI, pred, documentIRI );
	}

	@Override
	public Float getFloat( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getFloat( resourceIRI, pred, documentIRI );
	}

	@Override
	public Float getFloat( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getFloat( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Float> getFloats( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getFloats( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Float> getFloats( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getFloats( resourceIRI, pred, documentIRI );
	}

	@Override
	public Integer getInteger( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getInteger( resourceIRI, pred, documentIRI );
	}

	@Override
	public Integer getInteger( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getInteger( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Integer> getIntegers( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIntegers( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Integer> getIntegers( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIntegers( resourceIRI, pred, documentIRI );
	}

	@Override
	public Long getLong( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getLong( resourceIRI, pred, documentIRI );
	}

	@Override
	public Long getLong( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getLong( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Long> getLongs( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getLongs( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Long> getLongs( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getLongs( resourceIRI, pred, documentIRI );
	}

	@Override
	public Short getShort( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getShort( resourceIRI, pred, documentIRI );
	}

	@Override
	public Short getShort( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getShort( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Short> getShorts( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getShorts( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<Short> getShorts( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getShorts( resourceIRI, pred, documentIRI );
	}

	@Override
	public String getString( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getString( resourceIRI, pred, documentIRI );
	}

	@Override
	public String getString( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getString( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<String> getStrings( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getStrings( resourceIRI, pred, documentIRI );
	}

	@Override
	public Set<String> getStrings( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getStrings( resourceIRI, pred, documentIRI );
	}

	@Override
	public String getString( IRI resourceIRI, IRI pred, Set<String> languages ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getString( resourceIRI, pred, documentIRI, languages );
	}

	@Override
	public String getString( IRI resourceIRI, RDFNodeEnum pred, Set<String> languages ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getString( resourceIRI, pred, documentIRI, languages );
	}

	@Override
	public Set<String> getStrings( IRI resourceIRI, IRI pred, Set<String> languages ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getStrings( resourceIRI, pred, documentIRI, languages );
	}

	@Override
	public Set<String> getStrings( IRI resourceIRI, RDFNodeEnum pred, Set<String> languages ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getStrings( resourceIRI, pred, documentIRI, languages );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, Value obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI predicate, Collection<Value> values ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, predicate, values, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, boolean obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, byte obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, DateTime obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, double obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, float obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, int obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, long obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, short obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, String obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void add( IRI resourceIRI, IRI pred, String obj, String language ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		IRI documentIRI = getDocumentIRI( resourceIRI );
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.add( resourceIRI, pred, literal, documentIRI ) );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, RDFNodeEnum pred ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, Value obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI predicate, Set<Value> values ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, predicate, values, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, boolean obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, byte obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, DateTime obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, double obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, float obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, int obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, long obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, short obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, String obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void remove( IRI resourceIRI, IRI pred, String obj, String language ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.remove( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, Value obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, boolean obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, byte obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, DateTime obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, double obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, float obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, int obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, long obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, short obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, String obj ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public void set( IRI resourceIRI, IRI pred, String obj, String language ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.set( resourceIRI, pred, obj, documentIRI );
	}

	@Override
	public boolean hasType( IRI resourceIRI, IRI type ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.hasType( resourceIRI, type, documentIRI );
	}

	@Override
	public boolean hasType( IRI resourceIRI, RDFNodeEnum type ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.hasType( resourceIRI, type, documentIRI );
	}

	@Override
	public Set<IRI> getTypes( IRI resourceIRI ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		return super.getIRIs( resourceIRI, RDFResourceDescription.Property.TYPE, documentIRI );
	}

	@Override
	public void addType( IRI resourceIRI, IRI type ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.add( resourceIRI, RDFResourceDescription.Property.TYPE.getIRI(), type, documentIRI );
	}

	@Override
	public void removeType( IRI resourceIRI, IRI type ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.removeType( resourceIRI, type, documentIRI );
	}

	@Override
	public void setType( IRI resourceIRI, IRI type ) {
		IRI documentIRI = getDocumentIRI( resourceIRI );
		super.setType( resourceIRI, type, documentIRI );
	}

	private IRI getDocumentIRI( IRI resourceIRI ) {
		if ( ! IRIUtil.hasFragment( resourceIRI ) ) return resourceIRI;
		return SimpleValueFactory.getInstance().createIRI( IRIUtil.getDocumentIRI( resourceIRI.stringValue() ) );
	}

}
