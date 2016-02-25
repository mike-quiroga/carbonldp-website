package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import org.openrdf.repository.Repository;

/**
 * @author NestorVenegas
 * @since 0.15.1-ALPHA
 */
public class UpdateAction1o2o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "update-1o2o0.trig";

	@Override
	public void execute() throws Exception {
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
	}
}
