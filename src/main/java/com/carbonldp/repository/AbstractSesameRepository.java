package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import org.openrdf.spring.SesameConnectionFactory;

public class AbstractSesameRepository extends AbstractComponent {
	protected final SesameConnectionFactory connectionFactory;
	protected final ConnectionRWTemplate connectionTemplate;

	public AbstractSesameRepository(SesameConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}
}
