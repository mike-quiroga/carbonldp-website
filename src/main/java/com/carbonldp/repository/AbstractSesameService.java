package com.carbonldp.repository;

import org.openrdf.spring.SesameConnectionFactory;

public abstract class AbstractSesameService extends AbstractSesameRepository {

	public AbstractSesameService(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

}
