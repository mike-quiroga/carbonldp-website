package com.carbonldp.ldp.containers;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.models.Infraction;
import com.carbonldp.namespaces.C;
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
	protected void addMembers( URI targetUri, AddMembersAction members ) {
		isAddMembersAction( members );
		validate( members );
		containerService.addMembers( targetUri, members.getMembers() );
	}

	private void isAddMembersAction( AddMembersAction toValidate ) {
		if ( ! AddMembersActionFactory.getInstance().is( toValidate ) ) throw new InvalidResourceException( new Infraction( 0x2001, "rdf.type", C.Classes.ADD_MEMBER ) );
	}

	protected void validate( MembersAction membersAction ) {
		List<Infraction> infractions = MembersActionFactory.getInstance().validate( membersAction );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}
}
