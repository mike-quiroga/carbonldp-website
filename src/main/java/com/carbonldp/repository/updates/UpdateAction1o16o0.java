package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.utils.IRIUtil;
import org.eclipse.rdf4j.model.IRI;

import java.util.Arrays;
import java.util.Set;

/**
 * enable create executions for appAdmin
 *
 * @author NestorVenegas
 * @since 0.40.0
 */
public class UpdateAction1o16o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		Set<App> apps = getAllApps();
		apps.forEach( this::addCreateChildPermissionInJobsForAppAdmin );
	}

	public void addCreateChildPermissionInJobsForAppAdmin( App app ) {
		IRI appRoleContainerIRI = transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> appRoleRepository.getContainerIRI() );
		IRI appAdminIRI = IRIUtil.createChildIRI( appRoleContainerIRI, "app-admin/" );
		AppRole appAdmin = transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> new AppRole( sourceRepository.get( appAdminIRI ) ) );
		IRI jobsContainerIRI = generateJobsContainerIRI( app );

		aclRepository.addInheritablePermissions( jobsContainerIRI, Arrays.asList( appAdmin ), Arrays.asList( ACEDescription.Permission.CREATE_CHILD ), true );
	}

	private IRI generateJobsContainerIRI( App app ) {
		String jobsString = Vars.getInstance().getJobsContainer();
		return IRIUtil.createChildIRI( app.getIRI(), jobsString );
	}

}
