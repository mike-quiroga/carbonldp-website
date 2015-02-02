package com.carbonldp.repository.services;

import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.carbonldp.AbstractService;

public abstract class AbstractSesameService extends AbstractService {

	@Autowired
	protected SesameConnectionFactory connectionFactory;
}
