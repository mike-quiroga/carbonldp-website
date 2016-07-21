package com.carbonldp.test.transaction;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.TransactionActionTemplate;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TransactionManagementIT extends AbstractIT {

	@Autowired
	private TransactionActionTemplate transactionTemplate;

	@Test
	public void rollBackTest() {
		IRI agentsContainerIRI = SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getAgentsContainerURL() );
		Resource subject = agentsContainerIRI;
		IRI predicate = AgentDescription.Property.EMAIL.getIRI();
		Value object = SimpleValueFactory.getInstance().createLiteral( "nestor@carbon.com" );
		Resource context = subject;

		try {
			transactionTemplate.writeInTransaction( ( connection ) -> {

				connection.add( subject, predicate, object, context );

				throw new RuntimeException();
			} );
		} catch ( Exception e ) {
			LOG.debug( "exception has been throwed" );
		}

		transactionTemplate.readInTransaction( ( connection ) -> {
			assertFalse( connection.hasStatement( subject, predicate, object, false, context ) );
			return null;
		} );

	}

	@Test
	public void commitReadAndRemoveTest() {
		IRI agentsContainerIRI = SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getAgentsContainerURL() );
		Resource subject = agentsContainerIRI;
		IRI predicate = AgentDescription.Property.EMAIL.getIRI();
		Value object = SimpleValueFactory.getInstance().createLiteral( "nestor@carbon.com" );
		;
		Resource context = subject;

		transactionTemplate.writeInTransaction( ( connection ) -> {

			connection.add( subject, predicate, object, context );
		} );
		transactionTemplate.readInTransaction( ( connection ) -> {
			assertTrue( connection.hasStatement( subject, predicate, object, false, context ) );
			return null;
		} );
		transactionTemplate.writeInTransaction( ( connection ) -> {
			connection.remove( subject, predicate, object, context );
			assertFalse( connection.hasStatement( subject, predicate, object, false, context ) );
		} );

	}

}
// @formatter:off

// @formatter:on

