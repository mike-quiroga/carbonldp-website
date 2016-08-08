package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.Arrays;
import java.util.Set;

/**
 * add default permissions to backups, ldap and jobs containers
 *
 * @author JorgeEspinosa
 * @since 0.40.0
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
				addDefaultPermissionsToBackupsContainer( adminRole, backupsACL );
				String jobsString = Vars.getInstance().getJobsContainer();
				IRI jobsIRI = valueFactory.createIRI( appString + jobsString );
				ACL jobsACL = aclRepository.getResourceACL( jobsIRI );
				addDefaultPermissionsToJobsContainer( adminRole, jobsACL );
				String ldapServersString = Vars.getInstance().getAppLDAPServerContainer();
				IRI ldapServersIRI = valueFactory.createIRI( appString + ldapServersString );
				ACL ldapServersACL = aclRepository.getResourceACL( ldapServersIRI );
				addDefaultPermissionsToLDAPContainer( adminRole, ldapServersACL );
			} );
		}
	}

	private void addDefaultPermissionsToBackupsContainer( AppRole appAdminRole, ACL backupContainerACL ) {
		aclRepository.grantPermissions( backupContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPLOAD
		), false );
		aclRepository.addInheritablePermissions( backupContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.DELETE,
			ACEDescription.Permission.DOWNLOAD
		), true );
	}

	private void addDefaultPermissionsToJobsContainer( AppRole appAdminRole, ACL jobsContainerACL ) {
		aclRepository.grantPermissions( jobsContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.CREATE_CHILD
		), false );
		aclRepository.addInheritablePermissions( jobsContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), true );
	}

	private void addDefaultPermissionsToLDAPContainer( AppRole appAdminRole, ACL jobsContainerACL ) {
		aclRepository.grantPermissions( jobsContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.CREATE_CHILD
		), false );
		aclRepository.addInheritablePermissions( jobsContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), true );
	}
}
