package com.carbonldp.repository.updates;

import com.carbonldp.Vars;

/**
 * create tickets container in platform
 *
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.36.0
 */
public class UpdateAction1o8o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "update-1o8o0.trig";

	@Override
	public void execute() throws Exception {
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
	}
}
