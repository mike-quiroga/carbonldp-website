package com.carbonldp.test.transaction;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.TransactionActionTemplate;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TransactionManagementIT extends AbstractIT {

	@Autowired
	private TransactionActionTemplate transactionTemplate;

	@Test
	public void rollBackTest() {
		URI agentsContainerURI = new URIImpl( Vars.getAgentsContainerURL() );
		Resource subject = agentsContainerURI;
		URI predicate = AgentDescription.Property.EMAIL.getURI();
		Value object = new LiteralImpl( "nestor@carbon.com" );
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
		URI agentsContainerURI = new URIImpl( Vars.getAgentsContainerURL() );
		Resource subject = agentsContainerURI;
		URI predicate = AgentDescription.Property.EMAIL.getURI();
		Value object = new LiteralImpl( "nestor@carbon.com" );
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

