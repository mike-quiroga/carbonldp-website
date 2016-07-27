package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o12o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> addManageSecurityPermissionsToAdmin() );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppContext( app, () -> addManageSecurityPermissionsToAdmin() );
		}
	}

	public void addManageSecurityPermissionsToAdmin() {

	}
}
