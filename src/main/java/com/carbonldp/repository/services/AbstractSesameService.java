package com.carbonldp.repository.services;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.AbstractService;

public abstract class AbstractSesameService extends AbstractService {
	protected final SesameConnectionFactory connectionFactory;

	public AbstractSesameService(SesameConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
