package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.utils.IRIUtil;
import org.eclipse.rdf4j.model.IRI;

import java.util.Arrays;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o12o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> addManageSecurityPermissionsToAppAdmin( app ) );
		}
	}

	public void addManageSecurityPermissionsToAppAdmin( App app ) {
		IRI appRolesContainer = appRoleRepository.getContainerIRI();
		IRI appAdminRoleIRI = IRIUtil.createChildIRI( appRolesContainer, "app-admin/" );
		AppRole appRole = appRoleRepository.get( appAdminRoleIRI );
		aclRepository.grantPermissions( app.getRootContainerIRI(), Arrays.asList( appRole ), Arrays.asList( ACEDescription.Permission.MANAGE_SECURITY ),false );
		aclRepository.addInheritablePermissions( app.getRootContainerIRI(), Arrays.asList( appRole ), Arrays.asList( ACEDescription.Permission.MANAGE_SECURITY ), true );
	}
}
