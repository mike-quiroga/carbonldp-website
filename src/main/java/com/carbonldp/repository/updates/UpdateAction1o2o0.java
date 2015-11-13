package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import org.openrdf.repository.Repository;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o2o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "update-1o2o0.trig";

	@Override
	public void execute() throws Exception {
		Repository platformRepository = getRepository( Vars.getInstance().getPlatformRepositoryDirectory() );
		loadResourcesFile( platformRepository, resourcesFile, Vars.getInstance().getHost() );
		closeRepository( platformRepository );
	}
}
