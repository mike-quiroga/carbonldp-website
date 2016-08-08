package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.containers.RemoveMembersAction;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since 0.40.0
 */
@RequestHandler
public class AppRoleAgentsDELETEHandler extends AbstractDELETERequestHandler {

	protected AppRoleService appRoleService;

	protected void executeAction( IRI targetIRI, RemoveMembersAction members ) {
		appRoleService.removeAgents( targetIRI, members.getMembers() );
	}

	protected void removeMembers( IRI targetIRI ) {
		appRoleService.removeAgents( targetIRI );
	}

	@Autowired
	public void setAppRoleService( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}
