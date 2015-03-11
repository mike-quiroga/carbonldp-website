package com.carbonldp.test.transaction;

import static org.testng.Assert.assertFalse;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.TransactionActionTemplate;
import com.carbonldp.utils.RDFNodeUtil;

@Transactional
public class TransactionManagementIT extends AbstractIT {

	@Autowired
	private TransactionActionTemplate transactionTemplate;

	private String query;

	private static final String findByEmail_selector;
	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL ) )
		;
		//@formatter:on
		findByEmail_selector = queryBuilder.toString();
	}

	@Test
	public void rollBackTest() {
		transactionTemplate.writeInTransaction((connection) -> {
			URI agentsContainerURI = new URIImpl(Vars.getAgentsContainerURL());

			Resource subject;
			Resource predicate;
			Resource object;
			connection.add(subject, predicate, object, contexts);

			throw new RuntimeException();
		});

		transactionTemplate.readInTransaction((connection) -> {
			assertFalse(connection.hasStatement(null, arg1, arg2, arg3, arg4));
		});

	}
}
// @formatter:off

// @formatter:on

