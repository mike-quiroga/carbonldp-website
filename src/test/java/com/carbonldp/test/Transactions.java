package com.carbonldp.test;

import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Transactions {
	protected final SesameConnectionFactory connectionFactory;

	public Transactions(SesameConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void transaction() {
		// TODO make the test
	}
}
