package com.carbonldp.sparql;

import com.carbonldp.repository.AbstractSesameService;
import org.openrdf.spring.SesameConnectionFactory;

public class SesameSPARQLService extends AbstractSesameService {

	public SesameSPARQLService(SesameConnectionFactory connectionFactory) {
		super( connectionFactory );
	}
}
