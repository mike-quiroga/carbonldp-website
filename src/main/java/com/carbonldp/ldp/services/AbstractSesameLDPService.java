package com.carbonldp.ldp.services;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.sesame.AbstractSesameService;

public abstract class AbstractSesameLDPService extends AbstractSesameService {

	public AbstractSesameLDPService(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

}
