package com.carbonldp.test.transaction;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.Transactions;

@Transactional
public class TransactionManagementIT extends AbstractIT {

	@Autowired
	private Transactions transactions;

	private String query;

	@Test
	public void testTest() {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
			.append("CONSTRUCT {").append(NEW_LINE)
			.append(TAB).append("?s ?p ?o").append(NEW_LINE)
			.append("} WHERE {").append(NEW_LINE)
			.append(TAB).append("GRAPH ?sourceURI {").append(NEW_LINE)
			.append(TAB).append(TAB).append("?s ?p ?o").append(NEW_LINE)
			.append(TAB).append("}").append(NEW_LINE)
			.append("}")
		;
		//@formatter:on
		query = queryBuilder.toString();
		transactions.transaction();

	}
}
// @formatter:off

// @formatter:on

