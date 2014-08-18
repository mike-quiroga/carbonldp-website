package com.base22.carbon.utils;

import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ModelUtilTest {
	
	static Model model;
	static Resource resource;
	static String rdf;
	static InputStream rdfInputStream;
	
	static String actual;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rdf = "<rdf:RDF "
				+ "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' "
				+ "xmlns:vcard='http://www.w3.org/2001/vcard-rdf/3.0#' "
				+ "xmlns:c= 'http://carbonldp.com/ns/v1/platform#'> "
				+ "<rdf:Description rdf:about='http://somewhere/JohnSmith'> "
				+ "<vcard:FN>John Smith</vcard:FN> "
				+ "<vcard:N rdf:nodeID=\"A0\"/> "
				+ "<c:created>12345678987654</c:created> "
				+ "<c:modified>12345678987654</c:modified> "
				+ "<c:has>12345678987654</c:has> "
				+ "<c:mappingRequest>12345678987654</c:mappingRequest> "
				+ "</rdf:Description> "
				+ "</rdf:RDF>";
		
		rdfInputStream = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
		model = ModelFactory.createDefaultModel();
		model.read(rdfInputStream, "");
		
		ModelUtil.removeServerManagedProperties(model);
		actual = model.toString();
	}
	
	@Test
	public void testRemoveServerManagedPropertiesCreated() {
		assertFalse(actual.contains("c:created"));
	}
	
	@Test
	public void testRemoveServerManagedPropertiesModified() {
		assertFalse(actual.contains("c:modified"));
	}
	
	@Test
	public void testRemoveServerManagedPropertiesHas() {	
		assertFalse(actual.contains("c:has"));
	}
	
	@Test
	public void testRemoveServerManagedPropertiesMappingRequest() {	
		assertFalse(actual.contains("c:mappingRequest"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		model = null;
	}

}
