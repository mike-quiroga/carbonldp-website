package com.base22.carbon.test.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.jena.riot.Lang;

import com.base22.carbon.ldp.RdfUtil;
import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class RDFtoJSONLDConverterConsole {

	
	public static final String INPUT_FILE = "C:\\workspaces\\wkspc-carbon\\carbon\\src\\test\\resources\\turtle\\errorResponse.ttl";
	
	public static void main(String[] args) {
		RDFtoJSONLDConverterConsole console = new RDFtoJSONLDConverterConsole();
		console.execute();
	}
	
	/**
	 * 
	 */
	public void execute() {
		
		JenaJSONLD.init();
	     
		InputStream in = FileManager.get().open(INPUT_FILE);
		
		
		try {
			Model m = RdfUtil.createInMemoryModel(in, Lang.TURTLE, "");
			
			
			// QUICK SANITY CHECK IN N-TRIPLES
			//m.write(System.out, "N-TRIPLE");
			
			StringWriter out = new StringWriter();
			m.write(out, Lang.JSONLD.getName());
			System.out.println( out.toString() );
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
}
