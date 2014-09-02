package com.base22.carbon.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.base22.carbon.repository.DB2RepositoryService;
import com.base22.carbon.sparql.SparqlQuery;
import com.base22.carbon.sparql.SparqlQueryException;
import com.base22.carbon.sparql.SparqlService;
import com.base22.carbon.sparql.SparqlQuery.TYPE;
import com.hp.hpl.jena.query.ResultSet;

public class SparqlServiceTest {

	static SparqlService sparqlService;
	static DB2RepositoryService db2;

	static final String SCHEMA = "db2admin";
	static final String DATASET = "MAIN";

	static SparqlQuery sparqlQuery;
	static ResultSet resultSet;

	@BeforeClass
	public static void setUpBeforeClass() {
		sparqlService = new SparqlService();
		db2 = new DB2RepositoryService();
		sparqlQuery = new SparqlQuery();

		db2.setDbUrl("jdbc:db2://localhost:50000/CTEST");
		db2.setDbUsername("db2admin");
		db2.setDbPassword("db2admin");

		sparqlService.setRepositoryService(db2);
		resultSet = null;

		sparqlQuery.setDataset(DATASET);
	}

	@Test
	public void testValidateQueryValid() throws Exception {
		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery.setQuery("SELECT ?q ?p ?o { ?q ?p ?o }");

		assertTrue("Validate should return true after a valid query", sparqlService.validate(sparqlQuery));
	}

	@Test(expected = com.hp.hpl.jena.query.QueryParseException.class)
	public void testValidateQueryInvalid() throws Exception {
		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery.setQuery("SELECT ?q ?p ?o ?m { ?q }");

		assertTrue("Validate should actually throw an exception", sparqlService.validate(sparqlQuery));
	}

	@Test
	public void testValidateUpdateValid() throws Exception {
		sparqlQuery.setType(TYPE.UPDATE);
		sparqlQuery
				.setQuery("prefix vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> INSERT DATA {<http://base22.com/carbon/ldp/main/people/carlos> vcard:givenname \"carlos\"}");

		assertTrue("Validate should return true after a valid update query", sparqlService.validate(sparqlQuery));
	}

	@Test(expected = com.hp.hpl.jena.query.QueryParseException.class)
	public void testValidateUpdateInvalid() throws Exception {
		sparqlQuery.setType(TYPE.UPDATE);
		sparqlQuery.setQuery("INSERT {\"ejemplo\" \"ejemplo\" \"ejemplo\" \"ejemplo\"} { ?q ?p ?o }");

		assertTrue("Validate should actually throw an exception", sparqlService.validate(sparqlQuery));
	}

	@Test
	public void testSelectValid() throws Exception {
		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery.setQuery("SELECT ?q ?p ?o { ?q ?p ?o }");

		assertNotNull("SELECT query should not return a null resultSet", sparqlService.select(sparqlQuery));
	}

	@Test(expected = SparqlQueryException.class)
	public void testSelectInvalid() throws Exception {
		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery.setQuery("SELECT ?q ?p ?o ?m { ?q }");

		resultSet = sparqlService.select(sparqlQuery);
	}

	@Test
	public void testConstruct() throws Exception {
		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery
				.setQuery("prefix vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> CONSTRUCT { <http://base22.com/carbon/ldp/main/people/carlos> vcard:LN ?lastname } WHERE { ?s vcard:lastname ?lastname }");

		assertNotNull("CONSTRUCT query should not return a null resultModel", sparqlService.construct(sparqlQuery));
	}

	@Test
	public void testDescribe() throws Exception {
		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery.setQuery("DESCRIBE <http://base22.com/carbon/ldp/main/people/carlos> ?p ?o");

		assertNotNull("DESCRIBE query should not return a null resultModel", sparqlService.describe(sparqlQuery));
	}

	@Test
	public void testUpdate() throws Exception {
		sparqlQuery.setType(TYPE.UPDATE);
		sparqlQuery
				.setQuery("prefix vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> INSERT DATA {<http://base22.com/carbon/ldp/main/people/carlos> vcard:FN \"carlos\" }");

		assertTrue("UPDATE method should return true for an executed query", sparqlService.update(sparqlQuery));
	}

	@Test
	public void testAskTrue() throws Exception {
		sparqlQuery.setType(TYPE.UPDATE);
		sparqlQuery
				.setQuery("prefix vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> INSERT DATA {<http://base22.com/carbon/ldp/main/people/carlos> vcard:FN \"carlos\" }");
		sparqlService.update(sparqlQuery);

		sparqlQuery.setType(TYPE.QUERY);
		sparqlQuery.setQuery("ASK { <http://base22.com/carbon/ldp/main/people/carlos> ?p ?o}");
		assertTrue("ASK method should return true for this executed query", sparqlService.ask(sparqlQuery));
	}

	@Test
	public void testAskFalse() throws Exception {
		sparqlQuery.setQuery("ASK {?q ?p \"asaafjngbvn\"}");
		assertFalse("ASK method should return false for this executed query", sparqlService.ask(sparqlQuery));
	}

	@AfterClass
	public static void cleanUpAfterClass() throws Exception {
		sparqlQuery.setType(TYPE.UPDATE);
		sparqlQuery.setQuery("DELETE WHERE { ?s ?p ?o }");
		sparqlService.update(sparqlQuery);
		db2.release();
	}
}
