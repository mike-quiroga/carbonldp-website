package com.base22.carbon.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SparqlUtilTest {

	String sparql;
	static Map<String, String> prefixes;

	static HashMap<String, String> expectedPrefixes;
	String expected;
	String actual;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		expectedPrefixes = new HashMap<String, String>();
	}

	@Test
	public void testGetNSPrefixes() {
		sparql = "prefix vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "prefix xcard: <http://xcard.com>";

		expectedPrefixes.put("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
		expectedPrefixes.put("xcard", "http://xcard.com");

		assertEquals(expectedPrefixes, SparqlUtil.getNSPrefixes(sparql));

		expectedPrefixes.clear();
	}

	@Test
	public void testGetNSPrefixesNoBrackets() {
		sparql = "prefix vcard:   http://www.w3.org/2001/vcard-rdf/3.0# " + "prefix xcard: http://xcard.com";

		assertTrue(SparqlUtil.getNSPrefixes(sparql).isEmpty());
	}

	@Test
	public void testGetNSPrefixesNoSpaces() {
		sparql = "prefix vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> " + "prefix xcard:<http://xcard.com>";

		assertTrue(SparqlUtil.getNSPrefixes(sparql).isEmpty());
	}

	@Test
	public void testSetNSPrefixes() {
		sparql = "DELETE WHERE { ?s ?p ?o }";
		expectedPrefixes.put("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
		expectedPrefixes.put("xcard", "http://xcard.com");
		expectedPrefixes.put("another", "http://another.com");

		expected = "prefix another:<http://another.com>" + "prefix xcard:<http://xcard.com> " + "prefix vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> "
				+ "DELETE WHERE { ?s ?p ?o }";

		expected = expected.replaceAll("\\s+", "").toLowerCase();
		actual = SparqlUtil.setNSPrefixes(expectedPrefixes, sparql).replaceAll("\\s+", "").toLowerCase();

		assertEquals(expected, actual);

		expectedPrefixes.clear();
	}

	@Test
	public void testSetNSPrefixesNull() {
		sparql = "DELETE WHERE { ?s ?p ?o }";

		expected = "DELETE WHERE { ?s ?p ?o }";

		expected = expected.replaceAll("\\s+", "").toLowerCase();
		actual = SparqlUtil.setNSPrefixes(expectedPrefixes, sparql).replaceAll("\\s+", "").toLowerCase();

		assertEquals(expected, actual);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		expectedPrefixes.clear();
	}

}
