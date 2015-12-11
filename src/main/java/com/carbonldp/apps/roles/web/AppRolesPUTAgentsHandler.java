package com.carbonldp.apps.roles.web;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.containers.AbstractPUTRequestHandler;
import com.carbonldp.ldp.containers.AddMembersAction;
import com.carbonldp.ldp.containers.AddMembersActionFactory;
import com.carbonldp.ldp.containers.MembersAction;
import com.carbonldp.models.Infraction;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

import java.util.List;

/**
 * @author NestorEstrada
 * @since 0.19.0-ALPHA
 */

@RequestHandler
public class AppRolesPUTAgentsHandler extends AbstractPUTRequestHandler {

	@Override
	protected void executeAction( URI targetUri, AddMembersAction members ) {
		validate( members );
		appRoleService.addAgentMembers( targetUri, members.getMembers() );
	}

	protected void validate( MembersAction membersAction ) {
		List<Infraction> infractions = AddMembersActionFactory.getInstance().validate( membersAction );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}
}
