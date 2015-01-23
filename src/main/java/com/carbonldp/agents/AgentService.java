package com.carbonldp.agents;

import static com.carbonldp.commons.Consts.NEW_LINE;
import static com.carbonldp.commons.Consts.TAB;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;

import com.carbonldp.authentication.RunWith;
import com.carbonldp.repository.services.AbstractSesameService;

public class AgentService extends AbstractSesameService {

	@Value("${platform.agents.container}")
	private String agentContainerURI;

	public AgentService(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
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

	@RunWith(roles = { "SYSTEM" })
	@PreAuthorize("")
	public Agent findByEmail(String email) {
		return null;
	}

	public Agent findByURI(URI uri) {
		// TODO
		return null;
	}

}
