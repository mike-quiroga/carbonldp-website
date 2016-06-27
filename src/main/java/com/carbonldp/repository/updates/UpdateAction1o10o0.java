package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleValueFactory;

import java.util.Arrays;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class UpdateAction1o10o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			AppRole adminRole = transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
				IRI adminRoleIRI = SimpleValueFactory.getInstance().createIRI( appRoleRepository.getContainerIRI().stringValue() + "app-admin/" );
				return new AppRole( sourceRepository.get( adminRoleIRI ) );
			} );
			transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
				String appString = app.getIRI().stringValue();
				String backupsString = Vars.getInstance().getBackupsContainer();
				IRI backupsIRI = valueFactory.createIRI( appString + backupsString );
				ACL backupsACL = aclRepository.getResourceACL( backupsIRI );
				addPermissionsToAppPlatformRepositories( adminRole, backupsACL );
				String jobsString = Vars.getInstance().getJobsContainer();
				IRI jobsIRI = valueFactory.createIRI( appString + jobsString );
				ACL jobsACL = aclRepository.getResourceACL( jobsIRI );
				addPermissionsToAppPlatformRepositories( adminRole, jobsACL );
				String ldapServersString = Vars.getInstance().getAppLDAPServerContainer();
				IRI ldapServersIRI = valueFactory.createIRI( appString + ldapServersString );
				ACL ldapServersACL = aclRepository.getResourceACL( ldapServersIRI );
				addPermissionsToAppPlatformRepositories( adminRole, ldapServersACL );
			} );
		}
	}

	private void addPermissionsToAppPlatformRepositories( AppRole appAdminRole, ACL rootContainerACL ) {
		aclRepository.grantPermissions( rootContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.CREATE_ACCESS_POINT,
			ACEDescription.Permission.CREATE_CHILD,
			ACEDescription.Permission.UPLOAD,
			ACEDescription.Permission.DOWNLOAD,
			ACEDescription.Permission.EXTEND,
			ACEDescription.Permission.ADD_MEMBER,
			ACEDescription.Permission.REMOVE_MEMBER
		), false );
		aclRepository.addInheritablePermissions( rootContainerACL, Arrays.asList( appAdminRole ), Arrays.asList( ACEDescription.Permission.values() ), true );
	}
}
