package com.carbonldp.repository;

import org.openrdf.spring.SesameConnectionFactory;

public class SesameRDFResourceRepository extends AbstractSesameRepository implements RDFResourceRepository {

	public SesameRDFResourceRepository(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

}
