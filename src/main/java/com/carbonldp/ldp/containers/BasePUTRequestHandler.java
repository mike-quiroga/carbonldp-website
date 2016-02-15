package com.carbonldp.ldp.containers;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

import java.util.List;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
@RequestHandler( "container:basePUTRequestHandler" )
public class BasePUTRequestHandler extends AbstractPUTRequestHandler<RDFResource> {

	@Override
	protected void executeAction( URI targetUri, AddMembersAction members ) {
		validate( members );
		containerService.addMembers( targetUri, members.getMembers() );
	}

	protected void validate( AddMembersAction membersAction ) {
		List<Infraction> infractions = AddMembersActionFactory.getInstance().validate( membersAction );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}
}
