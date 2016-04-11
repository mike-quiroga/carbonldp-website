package com.carbonldp.agents.platform.web;

import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.IRI;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

@RequestHandler
public class PlatformAgentsDELETEHandler extends AbstractDELETERequestHandler {

	@Override
	public void delete( IRI targetIRI ) {
		// TODO: delete membership
		// TODO:delete from agents container
		// TODO: delete ACL
		throw new NotImplementedException();
	}

}
