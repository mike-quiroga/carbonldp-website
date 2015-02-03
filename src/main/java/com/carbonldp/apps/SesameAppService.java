package com.carbonldp.apps;

import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.sesame.AbstractSesameService;

public class SesameAppService extends AbstractSesameService implements AppService {

	public SesameAppService(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

}
