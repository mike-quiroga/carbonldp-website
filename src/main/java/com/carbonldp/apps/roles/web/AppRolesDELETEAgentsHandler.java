package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.containers.RemoveMembersAction;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since 0.21.0-ALPHA
 */
@RequestHandler
public class AppRolesDELETEAgentsHandler extends AbstractDELETERequestHandler {

	AppRoleService appRoleService;

	@Override
	protected void removeSelectiveMembers( RDFDocument requestDocument, URI targetURI ) {
		validateRequestDocument( requestDocument );

		RemoveMembersAction members = new RemoveMembersAction( requestDocument.getBaseModel(), requestDocument.subjectResource() );
		validate( members );

		appRoleService.removeAgentMembers( targetURI, members.getMembers() );
	}

	@Autowired
	public AppRolesDELETEAgentsHandler( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}
