package com.carbonldp.repository.updates;

import com.carbonldp.Vars;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o8o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "update-1o8o0.trig";

	@Override
	public void execute() throws Exception {
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
	}
}
