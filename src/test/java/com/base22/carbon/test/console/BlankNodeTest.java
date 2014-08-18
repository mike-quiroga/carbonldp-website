package com.base22.carbon.test.console;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class BlankNodeTest {

	public static void main(String[] args) {
		BlankNodeTest test = new BlankNodeTest();
		JenaJSONLD.init();
		test.execute();
	}

	public void execute() {
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.getResource("http://resource.com/");
		StmtIterator iterator = resource.listProperties();
		if ( ! iterator.hasNext() ) {
			System.out.println("It is null.");
		}
		RDFDataMgr.write(System.out, model, Lang.TURTLE);
	}

}