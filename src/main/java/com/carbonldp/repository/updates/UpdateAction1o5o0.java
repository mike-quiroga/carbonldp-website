package com.carbonldp.repository.updates;

import com.carbonldp.Vars;

/**
 * add acl to platform/apps/
 *
 * @author JorgeEspinosa
 * @since 0.28.3-ALPHA
 */
public class UpdateAction1o5o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "update-1o5o0.trig";

	@Override
	public void execute() throws Exception {
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
	}
}
