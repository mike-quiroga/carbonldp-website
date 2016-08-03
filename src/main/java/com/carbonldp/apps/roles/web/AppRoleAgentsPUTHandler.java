package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.containers.AbstractPUTRequestHandler;
import com.carbonldp.ldp.containers.AddMembersAction;
import com.carbonldp.ldp.containers.AddMembersActionFactory;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author NestorEstrada
 * @since 0.19.0-ALPHA
 */
@RequestHandler
public class AppRoleAgentsPUTHandler extends AbstractPUTRequestHandler<RDFResource> {

	protected AppRoleService appRoleService;

	@Override
	protected void executeAction( IRI targetIRI, AddMembersAction members ) {
		validate( members );
		appRoleService.addAgents( targetIRI, members.getMembers() );
	}

	protected void validate( AddMembersAction membersAction ) {
		List<Infraction> infractions = AddMembersActionFactory.getInstance().validate( membersAction );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setAppRoleService( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}