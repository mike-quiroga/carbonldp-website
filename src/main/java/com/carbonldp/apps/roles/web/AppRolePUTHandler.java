package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.containers.AbstractPUTRequestHandler;
import com.carbonldp.ldp.containers.AddMembersAction;
import com.carbonldp.ldp.containers.AddMembersActionFactory;
import com.carbonldp.ldp.containers.MembersAction;
import com.carbonldp.models.Infraction;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author NestorVenegas
 * @since _version_
 */
@RequestHandler
public class AppRolePUTHandler extends AbstractPUTRequestHandler<AppRole> {

	AppRoleService appRoleService;

	@Override
	protected void executeAction( URI targetUri, AddMembersAction members ) {
		validate( members );
		appRoleService.addChildMembers( targetUri, members.getMembers() );
	}

	protected void validate( MembersAction membersAction ) {
		List<Infraction> infractions = AddMembersActionFactory.getInstance().validate( membersAction );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setAppRoleService( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}
