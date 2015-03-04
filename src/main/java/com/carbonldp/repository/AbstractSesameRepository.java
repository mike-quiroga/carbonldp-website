package com.carbonldp.repository;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.AbstractComponent;

public class AbstractSesameRepository extends AbstractComponent {
	protected final SesameConnectionFactory connectionFactory;
	protected final ConnectionRWTemplate connectionTemplate;

	public AbstractSesameRepository(SesameConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		connectionTemplate = new ConnectionRWTemplate(connectionFactory);
	}
}
