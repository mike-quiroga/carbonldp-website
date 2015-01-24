package com.carbonldp.test.repository.services;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.carbonldp.commons.utils.LiteralUtil;
import com.carbonldp.commons.utils.ValueUtil;
import com.carbonldp.repository.ReadTransactionCallback;
import com.carbonldp.repository.ReadTransactionTemplate;
import com.carbonldp.repository.WriteTransactionCallback;
import com.carbonldp.repository.WriteTransactionTemplate;
import com.carbonldp.test.AbstractIT;

public class LocalRepositoryServiceIT extends AbstractIT {
	@Value("${repositories.directory}")
	private String directory;

	private final String dummyRepositoryID = "dummy-repository";

	@BeforeMethod
	public void tearDummyRepository() {
		if ( repositoryService.repositoryExists(dummyRepositoryID) ) {
			repositoryService.deleteRepository(dummyRepositoryID);
		}
	}

	@Test
	public void createRepository() {
		repositoryService.createRepository(dummyRepositoryID);

		File repositoryDirectory = new File(directory + "/repositories/" + dummyRepositoryID);
		assertTrue(repositoryDirectory.exists());
		assertFalse(repositoryDirectory.isFile());
	}

	@Test
	public void repositoryExists() {
		assertTrue(repositoryService.repositoryExists(testRepositoryID));

		File testDirectory = new File(directory + "/repositories/" + testRepositoryID);
		assertTrue(testDirectory.exists());
		assertFalse(testDirectory.isFile());

		assertFalse(repositoryService.repositoryExists(dummyRepositoryID));

		File dummyDirectory = new File(directory + "/repositories/" + dummyRepositoryID);
		assertFalse(dummyDirectory.exists());
	}

	@Test
	public void getReadTransactionTemplate() {
		ReadTransactionTemplate<boolean[]> template = repositoryService.getReadTransactionTemplate(testRepositoryID);
		boolean[] results = template.execute(new ReadTransactionCallback<boolean[]>() {

			@Override
			public boolean[] executeInTransaction(RepositoryConnection connection) throws Exception {
				boolean[] results = new boolean[3];

				results[0] = connection != null;
				results[1] = connection.isOpen();

				// TODO: Move these resources into a static class
				Resource testResource = valueFactory.createURI(testResourceURI);
				RepositoryResult<Statement> statements = connection.getStatements(testResource, null, null, false, testResource);
				results[2] = statements.hasNext();

				return results;
			}
		});

		// Connection is available
		assertTrue(results[0]);
		// Connection is open
		assertTrue(results[1]);
		// Connection can retrieve data
		assertTrue(results[2]);
	}

	@Test
	public void getWriteTransactionTemplate() {
		// TODO: Move these resources into a static class
		final Resource testResource = valueFactory.createURI(testResourceURI);
		final URI dummyProperty = valueFactory.createURI("http://www.example.org/vocabulary#dummy");
		final Literal dummyValue = valueFactory.createLiteral(true);

		WriteTransactionTemplate writeTemplate = repositoryService.getWriteTransactionTemplate(testRepositoryID);
		writeTemplate.addCallback(new WriteTransactionCallback() {

			@Override
			public void executeInTransaction(RepositoryConnection connection) throws Exception {
				connection.add(testResource, dummyProperty, dummyValue, testResource);
			}
		});

		writeTemplate.execute();

		ReadTransactionTemplate<Boolean> readTemplate = repositoryService.getReadTransactionTemplate(testRepositoryID);
		boolean valueWasPersisted = readTemplate.execute(new ReadTransactionCallback<Boolean>() {

			@Override
			public Boolean executeInTransaction(RepositoryConnection connection) throws Exception {
				// TODO: Move these resources into a static class
				RepositoryResult<Statement> statements = connection.getStatements(testResource, dummyProperty, null, false, testResource);

				if ( statements.hasNext() ) {
					Statement statement = statements.next();
					org.openrdf.model.Value object = statement.getObject();
					if ( ValueUtil.isLiteral(object) ) {
						Literal literal = ValueUtil.getLiteral(object);
						if ( LiteralUtil.isBoolean(literal) ) {
							return literal.booleanValue() == dummyValue.booleanValue();
						}
					}
				}
				return false;
			}
		});
		assertTrue(valueWasPersisted);

		// TODO: FT
	}

	@Test
	public void deleteRepository() {
		repositoryService.createRepository(dummyRepositoryID);

		File repositoryDirectory = new File(directory + "/repositories/" + dummyRepositoryID);
		assertTrue(repositoryDirectory.exists());
		assertFalse(repositoryDirectory.isFile());

		assertTrue(repositoryService.repositoryExists(dummyRepositoryID));

		repositoryService.deleteRepository(dummyRepositoryID);
		assertFalse(repositoryDirectory.exists());

		assertFalse(repositoryService.repositoryExists(dummyRepositoryID));
	}

}
