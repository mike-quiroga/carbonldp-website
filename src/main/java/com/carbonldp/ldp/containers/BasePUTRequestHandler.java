package com.carbonldp.ldp.containers;

import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
@RequestHandler( "container:basePUTRequestHandler" )
public class BasePUTRequestHandler extends AbstractPUTRequestHandler<RDFResource> {

	@Override
	protected void addMembers( URI targetUri, AddMembersAction members ) {
		containerService.addMembers( targetUri, members );
	}
}
