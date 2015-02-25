package com.carbonldp.sparql;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.AbstractSesameService;

public class SesameSPARQLService extends AbstractSesameService {

	public SesameSPARQLService(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}
}
