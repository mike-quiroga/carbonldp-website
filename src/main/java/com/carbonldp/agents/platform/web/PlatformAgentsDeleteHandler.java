package com.carbonldp.agents.platform.web;

import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since _version_
 */

@RequestHandler
public class PlatformAgentsDELETEHandler extends AbstractDELETERequestHandler {

	@Override
	public void delete( URI targetURI ) {
		// TODO: delete membership
		// TODO:delete from agents container
		// TODO: delete ACL
		throw new NotImplementedException();
	}

}
