package com.carbonldp.agents;

import static com.carbonldp.commons.Consts.NEW_LINE;
import static com.carbonldp.commons.Consts.TAB;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.repository.RDFDocumentRepository;

public class SesameAgentService extends AbstractSesameService implements AgentService {

	public SesameAgentService(SesameConnectionFactory connectionFactory, RDFDocumentRepository documentRepository) {
		super(connectionFactory, documentRepository);
	}

	private static final String findByEmailQuery;
	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
			.append("CONSTRUCT {").append(NEW_LINE)
			.append(TAB).append("?agent ?p ?o").append(NEW_LINE)
			.append("} WHERE {").append(NEW_LINE)
			.append(TAB).append("GRAPH ?agentContainerURI {").append(NEW_LINE)
			.append(TAB).append(TAB).append("?agentContainerURI").append(NEW_LINE)
			.append(TAB).append(TAB).append(TAB).append("a ")
			.append(TAB).append("}").append(NEW_LINE)
			.append("}")
		;
		//@formatter:on
		findByEmailQuery = queryBuilder.toString();
	}

	public Agent findByEmail(String email) {
		return null;
	}

	public Agent findByURI(URI uri) {
		// TODO
		return null;
	}

}
