package com.carbonldp.test;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;

@Test( groups = "unit-tests" )
public abstract class AbstractUT {
	private String testModelLocation = "test-model.trig";

	protected ValueFactory valueFactory;

	protected final String EXAMPLE_HOST = "http://example.org/";
	protected final String EXAMPLE_NS = "http://example.org/ns#";
	protected Model testModel;

	protected final String testAppURI = "http://carbonldp.com/apps/test-blog";
	protected final URI rootContainerURI = new URIImpl( testAppURI + "/" );
	protected final URI postsContainerURI = new URIImpl( testAppURI + "/posts/" );
	protected final String appFileDirectory = "/opt/carbon-test";
	protected final String platformFileDirectory = "/opt/carbon-test/platform/repository";

	@BeforeClass
	protected void init() {
		valueFactory = ValueFactoryImpl.getInstance();
		//testModel = new LinkedHashModel();
		testModel = readTestModel();
		setUp();
	}

	protected Model readTestModel() {
		Resource testModelResource = new ClassPathResource( testModelLocation );
		File testModelFile;
		try {
			testModelFile = testModelResource.getFile();
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}

		InputStream testModelIS;
		try {
			testModelIS = new FileInputStream( testModelFile );
		} catch ( FileNotFoundException e ) {
			throw new RuntimeException( e );
		}

		Model testModel;
		try {
			testModel = Rio.parse( testModelIS, "", RDFFormat.TRIG );
		} catch ( RDFParseException | UnsupportedRDFormatException | IOException e ) {
			throw new RuntimeException( e );
		}

		return testModel;
	}

	protected abstract void setUp();
}
