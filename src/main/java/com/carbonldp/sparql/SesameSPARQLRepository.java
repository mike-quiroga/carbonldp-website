package com.carbonldp.sparql;

import com.carbonldp.repository.AbstractSesameRepository;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;

public class SesameSPARQLRepository extends AbstractSesameRepository {

	public SesameSPARQLRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}
}
