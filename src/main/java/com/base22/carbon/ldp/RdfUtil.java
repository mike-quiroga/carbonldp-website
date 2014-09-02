package com.base22.carbon.ldp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.riot.Lang;

import com.base22.carbon.ConvertString;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;

public class RdfUtil {
	
	public static String getResourceParentURI(String uri) {
		String parentURI = null;

		char[] uriCharacters = uri.toCharArray();
		int i = (uriCharacters.length - 2);
		for (; uriCharacters[i] != '/'; i--) {
		}
		parentURI = uri.substring(0, i);

		return parentURI;
	}

	// TODO: Add support for defaultNSPrefixes in every format
	public static InputStream setDefaultNSPrefixes(InputStream inputStream, Lang language, boolean inputHasPriority) throws IOException {
		if ( language.equals(Lang.TURTLE) ) {
			return TurtleUtil.setDefaultNSPrefixesInTurtle(inputStream, inputHasPriority);
		} else {
			return inputStream;
		}
	}

	// Hack to retrieve the base Jena prepends to baseless resource URIs
	public static String retrieveJenaDefaultBase() throws IOException {
		String defaultBase = null;

		String bodytoParse = "<> a <http://www.w3.org/ns/ldp#Resource>.";
		InputStream inputStream = ConvertString.toInputStream(bodytoParse);
		Model emptyModel = createInMemoryModel(inputStream, Lang.TURTLE);
		ResIterator iterator = emptyModel.listSubjects();
		if ( iterator.hasNext() ) {
			defaultBase = iterator.next().getURI();
		}

		return defaultBase;
	}

	public static Model createInMemoryModel(InputStream inputStream, Lang language) throws IOException {
		return createInMemoryModel(inputStream, language, "");
	}

	public static Model createInMemoryModel(InputStream inputStream, Lang language, String baseURI) throws IOException {

		Model memModel = null;

		memModel = ModelFactory.createDefaultModel();
		memModel.read(inputStream, baseURI, language.getName());

		inputStream.close();

		return memModel;
	}
}
