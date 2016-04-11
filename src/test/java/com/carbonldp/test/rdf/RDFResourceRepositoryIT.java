package com.carbonldp.test.rdf;

import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.utils.LiteralUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public class RDFResourceRepositoryIT extends AbstractIT {
	private DateTimeFormatter formatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss" );

	@Autowired
	RDFResourceRepository resourceRepository;

	IRI subj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/" );
	ValueFactory factory = SimpleValueFactory.getInstance();

	@Test
	public void hasProperty_IRI_RDFNdeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		System.out.println( resourceRepository.toString() );

		assertEquals( resourceRepository.hasProperty( subj, pred ), true );
	}

	@Test
	public void hasProperty_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		assertEquals( resourceRepository.hasProperty( subj, pred ), true );
	}

	@Test
	public void contains_IRI_IRI_Value_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
	}

	@Test
	public void contains_IRI_RDFNodeEnum_Value_Test() {
		RDFNodeEnum pred = Property.B;
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
	}

	@Test
	public void contains_IRI_RDFNodeEnum_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		RDFNodeEnum obj = Property.A;
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
	}

	@Test
	public void getProperty_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getProperty( subj, pred ), obj );
	}

	@Test
	public void getProperty_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getProperty( subj, pred ), obj );
	}

	@Test
	public void getProperties_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getProperties( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getProperties_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getProperties( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getIRI_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getIRI( subj, pred ), obj );
	}

	@Test
	public void getIRI_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		assertEquals( resourceRepository.getIRI( subj, pred ), obj );
	}

	@Test
	public void getIRIs_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://carbonldp.com/ns/v1/platform#accessPoint" );
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getIRIs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getIRIs_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.B;
		IRI obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/comments/" );
		Iterator iterator = resourceRepository.getIRIs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getBoolean_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BOOLEAN;
		Boolean obj = false;
		assertEquals( resourceRepository.getBoolean( subj, pred ), obj );
	}

	@Test
	public void getBoolean_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#expired" );
		Boolean obj = false;
		assertEquals( resourceRepository.getBoolean( subj, pred ), obj );
	}

	@Test
	public void getBooleans_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BOOLEAN;
		Boolean obj = false;
		Iterator iterator = resourceRepository.getBooleans( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getBooleans_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#expired" );
		Boolean obj = false;
		Iterator iterator = resourceRepository.getBooleans( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getByte_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#no-editors" );
		Byte obj = 1;
		assertEquals( resourceRepository.getByte( subj, pred ), obj );
	}

	@Test
	public void getByte_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BYTE;
		Byte obj = 1;
		assertEquals( resourceRepository.getByte( subj, pred ), obj );
	}

	@Test
	public void getBytes_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#no-editors" );
		Byte obj = 1;
		Iterator iterator = resourceRepository.getBytes( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getBytes_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.BYTE;
		Byte obj = 1;
		Iterator iterator = resourceRepository.getBytes( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getDate_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#creation-date" );
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		assertEquals( resourceRepository.getDate( subj, pred ), obj );
	}

	@Test
	public void getDate_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DATE;
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		assertEquals( resourceRepository.getDate( subj, pred ), obj );
	}

	@Test
	public void getDates_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#creation-date" );
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		Iterator iterator = resourceRepository.getDates( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getDates_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DATE;
		DateTime obj = formatter.parseDateTime( "2014-12-25" );
		Iterator iterator = resourceRepository.getDates( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void getDouble_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#mb-used" );
		Double obj = new Double( "4856.2" );
		assertEquals( resourceRepository.getDouble( subj, pred ), obj );
	}

	@Test
	public void getDouble_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DOUBLE;
		Double obj = new Double( "4856.2" );
		assertEquals( resourceRepository.getDouble( subj, pred ), obj );
	}

	@Test
	public void getDoubles_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#mb-used" );
		Double obj = new Double( "4856.2" );
		Iterator iterator = resourceRepository.getDoubles( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getDoubles_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.DOUBLE;
		Double obj = new Double( "4856.2" );
		Iterator iterator = resourceRepository.getDoubles( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getFloat_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#wait-secs" );
		Float obj = new Float( 3.14 );
		assertEquals( resourceRepository.getFloat( subj, pred ), obj );
	}

	@Test
	public void getFloat_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.FLOAT;
		Float obj = new Float( 3.14 );
		assertEquals( resourceRepository.getFloat( subj, pred ), obj );
	}

	@Test
	public void getFloats_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#wait-secs" );
		Float obj = new Float( 3.14 );
		Iterator iterator = resourceRepository.getFloats( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getFloats_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.FLOAT;
		Float obj = new Float( 3.14 );
		Iterator iterator = resourceRepository.getFloats( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getInteger_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#post-no" );
		Integer obj = new Integer( 1 );
		assertEquals( resourceRepository.getInteger( subj, pred ), obj );
	}

	@Test
	public void getInteger_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.INTEGER;
		Integer obj = new Integer( 1 );
		assertEquals( resourceRepository.getInteger( subj, pred ), obj );
	}

	@Test
	public void getIntegers_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#post-no" );
		Integer obj = new Integer( 1 );
		Iterator iterator = resourceRepository.getIntegers( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getIntegers_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.INTEGER;
		Integer obj = new Integer( 1 );
		Iterator iterator = resourceRepository.getIntegers( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getShort_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#version" );
		Short obj = new Short( "1" );
		assertEquals( resourceRepository.getShort( subj, pred ), obj );
	}

	@Test
	public void getShort_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.SHORT;
		Short obj = new Short( "1" );
		assertEquals( resourceRepository.getShort( subj, pred ), obj );
	}

	@Test
	public void getShorts_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#version" );
		Short obj = new Short( "1" );
		Iterator iterator = resourceRepository.getShorts( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getShorts_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.SHORT;
		Short obj = new Short( "1" );
		Iterator iterator = resourceRepository.getShorts( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getLong_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#no-views" );
		Long obj = new Long( "2718281828" );
		assertEquals( resourceRepository.getLong( subj, pred ), obj );
	}

	@Test
	public void getLong_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.LONG;
		Long obj = new Long( "2718281828" );
		assertEquals( resourceRepository.getLong( subj, pred ), obj );
	}

	@Test
	public void getLongs_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#no-views" );
		Long obj = new Long( "2718281828" );
		Iterator iterator = resourceRepository.getLongs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getLongs_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.LONG;
		Long obj = new Long( "2718281828" );
		Iterator iterator = resourceRepository.getLongs( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getString_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#title" );
		String obj = "Post #1";
		assertEquals( resourceRepository.getString( subj, pred ), obj );
	}

	@Test
	public void getString_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post #1";
		assertEquals( resourceRepository.getString( subj, pred ), obj );
	}

	@Test
	public void getStrings_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#title" );
		String obj = "Post #1";
		Iterator iterator = resourceRepository.getStrings( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getStrings_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post #1";
		Iterator iterator = resourceRepository.getStrings( subj, pred ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getString_IRI_IRI_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#title" );
		String obj = "Post#1";
		assertEquals( resourceRepository.getString( subj, pred, languages ), obj );
	}

	@Test
	public void getString_IRI_RDFNodeEnum_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		RDFNodeEnum pred = Property.STRING;
		String obj = "Post#1";
		assertEquals( resourceRepository.getString( subj, pred, languages ), obj );
	}

	@Test
	public void getStrings_IRI_IRI_Set_Test() {
		Set<String> languages = new HashSet<>();
		languages.add( "en" );
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#title" );
		String obj = "Post#1";
		Iterator iterator = resourceRepository.getStrings( subj, pred, languages ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), obj );
	}

	@Test
	public void getStrings_IRI_RDFNodeEnum_Set_Test() {
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
	public void add_remove_IRI_IRI_Value_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		Value obj = SimpleValueFactory.getInstance().createIRI( "http://local.carbonldp.com/apps/test-blog/posts/post-1/sampleIRI" );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Boolean_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		boolean obj = true;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Byte_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		byte obj = 1;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_DateTime_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		DateTime obj = DateTime.now();
		Value literal = LiteralUtil.get( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Double_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		double obj = 5;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Float_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		float obj = 5.5f;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Int_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		int obj = 5;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Long_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		long obj = 5214;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_Short_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		short obj = 5;
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_String_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		String obj = "example";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void add_remove_IRI_IRI_String_String_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		String obj = "example";
		String language = "en";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, pred, obj, language );
		assertEquals( resourceRepository.contains( subj, pred, literal ), true );
		resourceRepository.remove( subj, pred, obj, language );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );

	}

	@Test
	public void remove_IRI_IRI_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void remove_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum pred = Property.EXAMPLE;
		IRI predIRI = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		String obj = "example1";
		Value literal = factory.createLiteral( obj );
		resourceRepository.add( subj, predIRI, obj );
		obj = "example2";
		resourceRepository.add( subj, predIRI, obj );
		resourceRepository.remove( subj, pred );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
		literal = factory.createLiteral( obj );
		assertEquals( resourceRepository.contains( subj, pred, literal ), false );
	}

	@Test
	public void set_IRI_IRI_Value_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Boolean_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Byte_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_DateTime_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Double_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Float_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Int_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Long_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_Short_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void set_IRI_IRI_String_Test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
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
	public void hasType_IRI_IRI_Test() {
		IRI type = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#BlogPost" );
		assertEquals( resourceRepository.hasType( subj, type ), true );
	}

	@Test
	public void hasType_IRI_RDFNodeEnum_Test() {
		RDFNodeEnum type = Property.TYPE;
		assertEquals( resourceRepository.hasType( subj, type ), true );
	}

	@Test
	public void getTypes_IRI_Test() {
		IRI type = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#BlogPost" );
		Iterator iterator = resourceRepository.getTypes( subj ).iterator();
		assertEquals( iterator.hasNext(), true );
		assertEquals( iterator.next(), type );
	}

	@Test
	public void addType_removeType_Test() {
		IRI type = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#BlogPostExample" );
		resourceRepository.addType( subj, type );
		assertEquals( resourceRepository.hasType( subj, type ), true );
		resourceRepository.removeType( subj, type );
		assertEquals( resourceRepository.hasType( subj, type ), false );
	}

	@Test
	public void setTypeTest() {
		IRI type = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#BlogPostExample" );
		IRI newType = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#BlogPostExampleSet" );
		resourceRepository.addType( subj, type );
		resourceRepository.setType( subj, newType );
		assertEquals( resourceRepository.hasType( subj, type ), false );
		assertEquals( resourceRepository.hasType( subj, newType ), true );
		resourceRepository.removeType( subj, newType );
	}

	private enum Property implements RDFNodeEnum {
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
		);

		private final IRI[] iris;

		Property( String... iris ) {
			if ( iris.length <= 0 ) throw new IllegalArgumentException( "At least one iri needs to be specified" );
			this.iris = new IRI[iris.length];
			int i = 0;
			for ( String iri : iris ) {
				this.iris[i] = SimpleValueFactory.getInstance().createIRI( iri );
				i++;
			}
		}

		@Override
		public IRI getIRI() {
			return this.iris[0];
		}

		@Override
		public IRI[] getIRIs() {
			return this.iris;
		}
	}
}


