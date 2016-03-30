package com.carbonldp.repository.updates;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppDescription;
import com.carbonldp.jobs.ExecutionDescription;
import com.carbonldp.jobs.JobDescription;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.namespaces.LDP;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o6o0 extends AbstractUpdateAction {

	@Override
	public void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInPlatformContext( () -> updateApp( app ) );
		}
	}

	private void updateApp( App app ) {
		createBackupContainer( app );
		createJobsContainer( app );
	}

	private void createBackupContainer( App app ) {
		String appString = app.getURI().stringValue();
		String backupsString = Vars.getInstance().getBackupsContainer();
		URI containerURI = new URIImpl( appString + backupsString );
		RDFResource backupsResource = new RDFResource( containerURI );
		BasicContainer backupsContainer = BasicContainerFactory.getInstance().create( backupsResource );
		containerRepository.createChild( app.getURI(), backupsContainer );
		aclRepository.createACL( backupsContainer.getURI() );
	}

	private void createJobsContainer( App app ) {
		String appString = app.getURI().stringValue();
		String jobsString = Vars.getInstance().getJobsContainer();
		URI containerURI = new URIImpl( appString + jobsString );
		RDFResource jobsResource = new RDFResource( containerURI );
		BasicContainer jobsContainer = BasicContainerFactory.getInstance().create( jobsResource, new URIImpl( LDP.Properties.MEMBER ), JobDescription.Property.EXECUTION_QUEUE_LOCATION.getURI() );
		jobsContainer = createQueue( jobsContainer );

		containerRepository.createChild( app.getURI(), jobsContainer );
		aclRepository.createACL( jobsContainer.getURI() );
	}

	private BasicContainer createQueue( BasicContainer jobsContainer ) {
		URI jobsExecutionQueue = new URIImpl( jobsContainer.getURI().stringValue() + Consts.HASH_SIGN + Vars.getInstance().getQueue() );
		jobsContainer.set( ExecutionDescription.List.QUEUE.getURI(), jobsExecutionQueue );
		jobsContainer.getBaseModel().add( jobsExecutionQueue, RDF.FIRST, jobsExecutionQueue, jobsContainer.getURI() );
		jobsContainer.getBaseModel().add( jobsExecutionQueue, RDF.REST, RDF.NIL, jobsContainer.getURI() );
		return jobsContainer;
	}
}

