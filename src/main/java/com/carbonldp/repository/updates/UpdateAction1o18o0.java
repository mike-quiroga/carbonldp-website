package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.jobs.ExecutionDescription;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.IRI;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o18o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			IRI jobsContainerIRI = generateJobsContainerIRI( app );
			if ( resourceRepository.getResource( jobsContainerIRI, ExecutionDescription.List.QUEUE ) == null ) continue;
			if ( ValueUtil.isBNode( resourceRepository.getResource( jobsContainerIRI, ExecutionDescription.List.QUEUE ) ) ) continue;
			transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> executionRepository.dequeue( jobsContainerIRI ) );
		}
	}

	private IRI generateJobsContainerIRI( App app ) {
		String appString = app.getIRI().stringValue();
		String jobsString = Vars.getInstance().getJobsContainer();
		return valueFactory.createIRI( appString + jobsString );
	}

}
