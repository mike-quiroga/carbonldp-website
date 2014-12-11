package com.carbonldp.test;

import java.io.File;
import java.io.IOException;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.carbonldp.repository.WriteTransactionCallback;
import com.carbonldp.repository.WriteTransactionTemplate;
import com.carbonldp.repository.services.RepositoryService;

@Test
//@formatter:off
@ContextHierarchy({
  @ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml" }),
  @ContextConfiguration(locations = { "classpath:test-config.xml" }),
})
//@formatter:on
public abstract class AbstractIT extends AbstractTestNGSpringContextTests {
	@Autowired
	protected RepositoryService repositoryService;

	protected final String testRepositoryID = "test-repository";
	protected final String testResourceURI = "http://carbonldp.com/apps/test-app/resource";

	protected final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

	private final String testDataLocation = "test-data.trig";

	@Override
	@BeforeSuite
	protected void springTestContextPrepareTestInstance() throws Exception {
		super.springTestContextPrepareTestInstance();
	}

	@BeforeTest
	public void setRepository() {
		if ( ! repositoryService.repositoryExists(testRepositoryID) ) {
			repositoryService.createRepository(testRepositoryID);
		}

		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(testRepositoryID);
		// Clean the repository
		template.addCallback(new WriteTransactionCallback() {

			@Override
			public void executeInTransaction(RepositoryConnection connection) throws RepositoryException {
				connection.clear();
			}
		});

		Resource testDataResoure = new ClassPathResource(testDataLocation);
		final File testDataFile;
		try {
			testDataFile = testDataResoure.getFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Add data
		template.addCallback(new WriteTransactionCallback() {

			@Override
			public void executeInTransaction(RepositoryConnection connection) throws Exception {
				connection.add(testDataFile, "", RDFFormat.TRIG);
			}
		});

		template.execute();
	}
}
