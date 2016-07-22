package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import com.carbonldp.sparql.SPARQLTemplate;
import com.carbonldp.utils.IRIUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;

public class AbstractSesameRepository extends AbstractComponent {
	protected final SesameConnectionFactory connectionFactory;
	protected final ConnectionRWTemplate connectionTemplate;
	protected final SPARQLTemplate sparqlTemplate;

	protected String containerSlug;

	public AbstractSesameRepository( SesameConnectionFactory connectionFactory ) {
		this.connectionFactory = connectionFactory;
		connectionTemplate = new ConnectionRWTemplate( connectionFactory );
		sparqlTemplate = new SPARQLTemplate( connectionFactory );
	}

	protected IRI getContainerIRI( IRI rootContainerIRI ) {
		return IRIUtil.createChildIRI( rootContainerIRI, containerSlug );
	}
}
