package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import com.carbonldp.sparql.SPARQLTemplate;
import org.openrdf.spring.SesameConnectionFactory;

public class AbstractSesameRepository extends AbstractComponent {
	protected final SesameConnectionFactory connectionFactory;
	protected final ConnectionRWTemplate connectionTemplate;
	protected final SPARQLTemplate sparqlTemplate;

	public AbstractSesameRepository( SesameConnectionFactory connectionFactory ) {
		this.connectionFactory = connectionFactory;
		connectionTemplate = new ConnectionRWTemplate( connectionFactory );
		sparqlTemplate = new SPARQLTemplate( connectionFactory );
	}


}
