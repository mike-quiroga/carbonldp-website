package com.carbonldp.test.rdf;

import com.carbonldp.rdf.PrefixedURI;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.utils.LiteralUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class RDFResourceRepositoryIT extends AbstractIT {
	private DateTimeFormatter formatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss" );

	@Autowired
	private RDFResourceRepository resourceRepository;

	URI subj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1" );
	ValueFactory factory = ValueFactoryImpl.getInstance();

	@Test
	public void hasProperty_URI_RDFNdeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		assertEquals( resourceRepository.hasProperty( subj, pred ), true );
	}

	@Test
	public void hasProperty_URI_URI_Test() {
		URI pred = new URIImpl( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		assertEquals( resourceRepository.hasProperty( subj, pred ), true );
	}

	@Test
	public void contains_URI_URI_Value_Test() {
		URI pred = new URIImpl( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
	}

	@Test
	public void contains_URI_RDFNodeEnum_Value_Test() {
		RDFNodeEnum pred = Property.B;
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
	}

	@Test
	public void contains_URI_RDFNodeEnum_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		RDFNodeEnum obj = Property.A;
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
	}

	@Test
	public void getProperty_URI_URI_Test() {
		URI pred = new URIImpl( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getProperty( subj, pred ), obj );
	}

	@Test
	public void getProperty_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getProperty( subj, pred ), obj );
	}

	@Test
	public void getProperties_URI_URI_Test() {
		URI pred = new URIImpl( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getProperties( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getProperties_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getProperties( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getURI_URI_URI_Test() {
		URI pred = new URIImpl( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getURI( subj, pred ), obj );
	}

	@Test
	public void getURI_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getURI( subj, pred ), obj );
	}

	@Test
	public void getURIs_URI_URI_Test() {
		URI pred = new URIImpl( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getURIs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getURIs_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		URI obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getURIs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getBoolean_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BOOLEAN;
		Boolean obj = false;
		assertEquals( resourceRepository.getBoolean( subj, pred ), obj );
	}

	@Test
	public void getBoolean_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#expired" );
		Boolean obj = false;
		assertEquals( resourceRepository.getBoolean( subj, pred ), obj );
	}

	@Test
	public void getBooleans_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BOOLEAN;
		Boolean obj = false;
		Iterator iterator = resourceRepository.getBooleans( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getBooleans_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#expired" );
		Boolean obj = false;
		Iterator iterator = resourceRepository.getBooleans( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getByte_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#no-editors" );
		Byte obj = 1;
		assertEquals( resourceRepository.getByte( subj, pred ), obj );
	}

	@Test
	public void getByte_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BYTE;
		Byte obj = 1;
		assertEquals( resourceRepository.getByte( subj, pred ), obj );
	}

	@Test
	public void getBytes_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#no-editors" );
		Byte obj = 1;
		Iterator iterator = resourceRepository.getBytes( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getBytes_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BYTE;
		Byte obj = 1;
		Iterator iterator = resourceRepository.getBytes( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getDate_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#creation-date" );
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		assertEquals( resourceRepository.getDate( subj, pred ), obj );
	}

	@Test
	public void getDate_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DATE;
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		assertEquals( resourceRepository.getDate( subj, pred ), obj );
	}

	@Test
	public void getDates_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#creation-date" );
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		Iterator iterator = resourceRepository.getDates( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getDates_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DATE;
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		Iterator iterator = resourceRepository.getDates( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getDouble_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#mb-used" );
		Double obj = new Double( "4856.2" );
		assertEquals( resourceRepository.getDouble( subj, pred ), obj );
	}

	@Test
	public void getDouble_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DOUBLE;
		Double obj = new Double( "4856.2" );
		assertEquals( resourceRepository.getDouble( subj, pred ), obj );
	}

	@Test
	public void getDoubles_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#mb-used" );
		Double obj = new Double( "4856.2" );
		Iterator iterator = resourceRepository.getDoubles( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getDoubles_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DOUBLE;
		Double obj = new Double( "4856.2" );
		Iterator iterator = resourceRepository.getDoubles( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getFloat_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#wait-secs" );
		Float obj = new Float( 3.14 );
		assertEquals( resourceRepository.getFloat( subj, pred ), obj );
	}

	@Test
	public void getFloat_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.FLOAT;
		Float obj = new Float( 3.14 );
		assertEquals( resourceRepository.getFloat( subj, pred ), obj );
	}

	@Test
	public void getFloats_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#wait-secs" );
		Float obj = new Float( 3.14 );
		Iterator iterator = resourceRepository.getFloats( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getFloats_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.FLOAT;
		Float obj = new Float( 3.14 );
		Iterator iterator = resourceRepository.getFloats( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getInteger_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#post-no" );
		Integer obj = new Integer( 1 );
		assertEquals( resourceRepository.getInteger( subj, pred ), obj );
	}

	@Test
	public void getInteger_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.INTEGER;
		Integer obj = new Integer( 1 );
		assertEquals( resourceRepository.getInteger( subj, pred ), obj );
	}

	@Test
	public void getIntegers_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#post-no" );
		Integer obj = new Integer( 1 );
		Iterator iterator = resourceRepository.getIntegers( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getIntegers_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.INTEGER;
		Integer obj = new Integer( 1 );
		Iterator iterator = resourceRepository.getIntegers( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getShort_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#version" );
		Short obj = new Short( "1" );
		assertEquals( resourceRepository.getShort( subj, pred ), obj );
	}

	@Test
	public void getShort_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.SHORT;
		Short obj = new Short( "1" );
		assertEquals( resourceRepository.getShort( subj, pred ), obj );
	}

	@Test
	public void getShorts_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#version" );
		Short obj = new Short( "1" );
		Iterator iterator = resourceRepository.getShorts( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getShorts_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.SHORT;
		Short obj = new Short( "1" );
		Iterator iterator = resourceRepository.getShorts( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getLong_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#no-views" );
		Long obj = new Long( "2718281828" );
		assertEquals( resourceRepository.getLong( subj, pred ), obj );
	}

	@Test
	public void getLong_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.LONG;
		Long obj = new Long( "2718281828" );
		assertEquals( resourceRepository.getLong( subj, pred ), obj );
	}

	@Test
	public void getLongs_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#no-views" );
		Long obj = new Long( "2718281828" );
		Iterator iterator = resourceRepository.getLongs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getLongs_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.LONG;
		Long obj = new Long( "2718281828" );
		Iterator iterator = resourceRepository.getLongs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getString_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#title" );
		String obj = "Post #1";
		assertEquals( resourceRepository.getString( subj, pred ), obj );
	}

	@Test
	public void getString_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post #1";
		assertEquals( resourceRepository.getString( subj, pred ), obj );
	}

	@Test
	public void getStrings_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#title" );
		String obj = "Post #1";
		Iterator iterator = resourceRepository.getStrings( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getStrings_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post #1";
		Iterator iterator = resourceRepository.getStrings( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getString_URI_URI_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		URI pred = new URIImpl( "http://example.org/ns#title" );
		String obj = "Post#1";
		assertEquals( resourceRepository.getString( subj, pred, languages ), obj );
	}

	@Test
	public void getString_URI_RDFNodeEnum_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post#1";
		assertEquals( resourceRepository.getString( subj, pred, languages ), obj );
	}

	@Test
	public void getStrings_URI_URI_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		URI pred = new URIImpl( "http://example.org/ns#title" );
		String obj = "Post#1";
		Iterator iterator = resourceRepository.getStrings( subj, pred, languages ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getStrings_URI_RDFNodeEnum_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post#1";
		Iterator iterator = resourceRepository.getStrings( subj, pred, languages ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void add_remove_URI_URI_Value_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		Value obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/sampleURI" );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), false );

	}

	@Test
	public void add_remove_URI_URI_Boolean_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		boolean obj = true;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_Byte_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		byte obj = 1;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_DateTime_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		DateTime obj = DateTime.now();
		Value literal = LiteralUtil.get( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_Double_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		double obj = 5;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_Float_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		float obj = 5.5f;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_Int_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		int obj = 5;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_Long_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		long obj = 5214;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_Short_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		short obj = 5;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_String_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "example";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_URI_URI_String_String_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "example";
		String language = "en";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj, language );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj, language );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void remove_URI_URI_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "example1";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		obj = "example2";
		resourceRepository.add( subj, pred, obj );
		resourceRepository.remove( subj, pred );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
	}

	@Test
	public void remove_URI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.EXAMPLE;
		URI predURI = new URIImpl( "http://example.org/ns#example" );
		String obj = "example1";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, predURI, obj );
		obj = "example2";
		resourceRepository.add( subj, predURI, obj );
		resourceRepository.remove( subj, pred );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
	}

	@Test
	public void set_URI_URI_Value_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		Value literal = factory.createLiteral( "set" );
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, literal );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Boolean_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		boolean objSet = true;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Byte_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		byte objSet = 1;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_DateTime_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		DateTime objSet = DateTime.now();
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = LiteralUtil.get( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Double_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		double objSet = 11234546;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Float_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		float objSet = 3.141592f;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Int_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		int objSet = 11234546;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Long_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		long objSet = 11234546;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_Short_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		short objSet = 112;
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void set_URI_URI_String_Test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		String obj = "add";
		String objSet = "set";
		String language = "en";
		resourceRepository.add( subj, pred, obj );
		resourceRepository.set( subj, pred, objSet, language );
		Value literal = factory.createLiteral( objSet );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		resourceRepository.remove( subj, pred );
	}

	@Test
	public void hasType_URI_URI_Test() {
		URI type = new URIImpl( "http://example.org/ns#BlogPost" );
		assertEquals( resourceRepository.hasType( subj, type ), true );
	}

	@Test
	public void hasType_URI_RDFNodeEnum_Test() {
		RDFNodeEnum type = Property.TYPE;
		assertEquals( resourceRepository.hasType( subj, type ), true );
	}

	@Test
	public void getTypes_URI_Test() {
		URI type = new URIImpl( "http://example.org/ns#BlogPost" );
		Iterator iterator = resourceRepository.getTypes( subj ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), type );
	}

	@Test
	public void addType_removeType_Test() {
		URI type = new URIImpl( "http://example.org/ns#BlogPostExample" );
		resourceRepository.addType( subj, type );
		assertEquals( resourceRepository.hasType( subj, type ), true );
		resourceRepository.removeType( subj, type );
		assertEquals( resourceRepository.hasType( subj, type ), false );
	}

	@Test
	public void setTypeTest() {
		URI type = new URIImpl( "http://example.org/ns#BlogPostExample" );
		URI newType = new URIImpl( "http://example.org/ns#BlogPostExampleSet" );
		resourceRepository.addType( subj, type );
		resourceRepository.setType( subj, newType );
		assertEquals( resourceRepository.hasType( subj, type ), false );
		assertEquals( resourceRepository.hasType( subj, newType ), true );
		resourceRepository.removeType( subj, newType );
	}

	private static enum Property implements RDFNodeEnum {
		//@formatter:off
		TYPE(
			"http://example.org/ns#BlogPost"
		),
		A(
			 "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/"
		),
		B(
			"http://carbonldp.com/ns/v1/platform#accessPoint"
		),
		BOOLEAN(
			"http://example.org/ns#expired"
		),
		BYTE(
			"http://example.org/ns#no-editors"
		),
		DATE(
			"http://example.org/ns#creation-date"
		),
		DOUBLE(
			"http://example.org/ns#mb-used"
		),
		FLOAT(
			"http://example.org/ns#wait-secs"
		),
		INTEGER(
			"http://example.org/ns#post-no"
		),
		SHORT(
			"http://example.org/ns#version"
		),
		LONG(
			"http://example.org/ns#no-views"
		),
		STRING(
			"http://example.org/ns#title"
		),
		EXAMPLE(
			"http://example.org/ns#example"
		)
		;
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;

		private Property( String... uris ) {
			if ( uris.length <= 0 ) throw new IllegalArgumentException( "At least one uri needs to be specified" );
			this.prefixedURIs = new PrefixedURI[uris.length];
			int i = 0;
			for ( String uri : uris ) {
				this.prefixedURIs[i] = new PrefixedURI( uri );
				i++;
			}
		}

		@Override
		public PrefixedURI getURI() {
			return this.prefixedURIs[0];
		}

		@Override
		public PrefixedURI[] getURIs() {
			return this.prefixedURIs;
		}
	}
}


