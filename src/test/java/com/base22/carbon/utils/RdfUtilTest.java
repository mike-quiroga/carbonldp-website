package com.base22.carbon.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RdfUtilTest {

	static String turtle;
	static Map<String, String> prefixes;
	
	static HashMap<String, String> expectedPrefixes;
	static String expected;
	static String actual;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		expectedPrefixes = new HashMap<String,String>();
	}
	
	// THESE METHODS UNDER TEST NO LONGER EXIST IN THE RdfUtil CLASS
	/*
	@Test
	public void testGetNSPrefixesInTurtle() {
		turtle = "@prefix vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#>. "
				+ "@prefix xcard: <http://xcard.com>.";
		
		expectedPrefixes.put("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
		expectedPrefixes.put("xcard", "http://xcard.com");
		
		assertEquals(expectedPrefixes, RdfUtil.getNSPrefixesInTurtle(turtle));
		
		expectedPrefixes.clear();
	}
	
	
	
	@Test
	public void testGetNSPrefixesInTurtleNoBrackets() {
		turtle = "@prefix vcard:   http://www.w3.org/2001/vcard-rdf/3.0#."
				+ "@prefix xcard: http://xcard.com.";

		assertTrue(RdfUtil.getNSPrefixesInTurtle(turtle).isEmpty());
	}
	
	@Test
	public void testGetNSPrefixesInTurtleNoSpaces() {
		turtle = "@prefix vcard:<http://www.w3.org/2001/vcard-rdf/3.0#>."
				+ "@prefix xcard:<http://xcard.com>.";
		
		assertTrue(RdfUtil.getNSPrefixesInTurtle(turtle).isEmpty());
	}
	
	
	@Test
	public void testSetNSPrefixesInTurtle() {
		turtle = "DELETE WHERE { ?s ?p ?o }";
		expectedPrefixes.put("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
		expectedPrefixes.put("xcard", "http://xcard.com");
		expectedPrefixes.put("another", "http://another.com");
		
		expected = "@prefix another: <http://another.com>."
				+ "@prefix xcard:<http://xcard.com>. "
				+ "@prefix vcard:<http://www.w3.org/2001/vcard-rdf/3.0#>. "
				+ "DELETE WHERE { ?s ?p ?o }";
		
		expected = expected.replaceAll("\\s+","").toLowerCase();
		actual = RdfUtil.setNSPrefixesInTurtle(expectedPrefixes, turtle).replaceAll("\\s+","").toLowerCase();
		
		assertEquals(expected, actual);
		
		expectedPrefixes.clear();
	}
	
	@Test
	public void testSetNSPrefixesInTurtleNull() {
		turtle = "DELETE WHERE { ?s ?p ?o }";
		
		expected = "DELETE WHERE { ?s ?p ?o }";
		
		expected = expected.replaceAll("\\s+","").toLowerCase();
		actual = RdfUtil.setNSPrefixesInTurtle(expectedPrefixes, turtle).replaceAll("\\s+","").toLowerCase();
		
		assertEquals(expected, actual);
	}
	*/
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		expectedPrefixes.clear();
	}
}