package com.base22.carbon.model;

import org.junit.Test;

import com.base22.carbon.models.TurtlePatch;

public class TurtlePatchTest {

	TurtlePatch turtlePatch;
	String toParse;
	String resourceToMatch; // requestURI

	@Test
	public void testOk() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNullInsidePrefix() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX" + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok\";"
				+ "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNullInsideInsert() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNullInsideDelete() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "} "

		+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testNoPrefix() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok\";"
				+ "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testNoDelete() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> "

		+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testNoInsert() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testEscapedConstructs() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX\tvcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE\tDATA {\n"
				+ "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "\t\t\t\tvcard:FN        \t   \"Patch Ok\";\n"
				+ "\t\t\t\tvcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "\t\t\t\tvcard:FN            \"Patch Ok Example\";"
				+ "\t\t\t\tvcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNoInsertDelete() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> ";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testInvertedTriples() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    \"Patch Ok\"			vcard:FN ;" + "    \"Pok\"				vcard:nickname. " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";
		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testNullInsertUri() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <>" + "    vcard:FN            \"Patch Ok Example\";" + "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testNullDeleteUri() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <>" + "    vcard:FN            \"Patch Ok\";"
				+ "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testDeleteUriNoMatch() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/Patch>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <>" + "    vcard:FN            \"Patch Ok Example\";" + "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testSintaxErrorUri() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <<http://base22.com/carbon/ldp/main/people/PatchOk>>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <<http://base22.com/carbon/ldp/main/people/PatchOk>>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testRequestUriSlash() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk/";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNoPunctuation() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\"" + "    vcard:nickname      \"Pok\" " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\""
				+ "    vcard:nickname      \"lePatch\" " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testPunctuationInMiddle() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN;           \"Patch Ok\"" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN;           \"Patch Ok Example\""
				+ "    vcard:nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNoQuotationMarks() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            Patch Ok;" + "    vcard:nickname      Pok. " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            Patch Ok Example;"
				+ "    vcard:nickname      lePatch. " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testOneParentheses() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "( PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> )" + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "}";
		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testInsertDelete() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:FN            \"Patch Ok Example\";"
				+ "    vcard:nickname      \"lePatch\". " + "} "

				+ "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:nickname      \"Pok\". " + "}";
		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test(expected = Exception.class)
	public void testNoProperty() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:            \"Patch Ok\";" + "    vcard:      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    :FN            \"Patch Ok Example\";"
				+ "    :nickname      \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}

	@Test
	public void testSameProperties() throws Exception {
		resourceToMatch = "http://base22.com/carbon/ldp/main/people/PatchOk";
		toParse = "" + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#> " + "DELETE DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>"
				+ "    vcard:FN            \"Patch Ok\";" + "    vcard:FN		      \"Pok\". " + "} "

				+ "INSERT DATA {" + "  <http://base22.com/carbon/ldp/main/people/PatchOk>" + "    vcard:lastname            \"Patch Ok Example\";"
				+ "    vcard:lastname		        \"lePatch\". " + "}";

		turtlePatch = new TurtlePatch(toParse, resourceToMatch);
	}
}
