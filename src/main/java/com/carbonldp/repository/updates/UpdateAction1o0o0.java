package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import org.openrdf.repository.Repository;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class UpdateAction1o0o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "platform-default.trig";

	@Override
	public void run() {
		Repository platformRepository = getRepository( Vars.getPlatformRepositoryDirectory() );
		emptyRepository( platformRepository );
		loadResourcesFile( platformRepository, resourcesFile, Vars.getHost() );
		closeRepository( platformRepository );
	}
}
