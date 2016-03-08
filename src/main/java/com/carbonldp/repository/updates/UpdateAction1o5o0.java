package com.carbonldp.repository.updates;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppDescription;
import com.carbonldp.jobs.JobDescription;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o5o0 extends AbstractUpdateAction {

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
		createJobsQueue( app );
	}

	private void createBackupContainer( App app ) {
		String appString = app.getURI().stringValue();
		String backupsString = Vars.getInstance().getBackupsContainer();
		URI containerURI = new URIImpl( appString + backupsString );
		RDFResource backupsResource = new RDFResource( containerURI );
		DirectContainer backupsContainer = DirectContainerFactory.getInstance().create( backupsResource, app.getURI(), AppDescription.Property.BACKUP.getURI() );
		sourceRepository.createAccessPoint( app.getURI(), backupsContainer );
		aclRepository.createACL( backupsContainer.getURI() );
	}

	private void createJobsContainer( App app ) {
		String appString = app.getURI().stringValue();
		String jobsString = Vars.getInstance().getJobsContainer();
		URI containerURI = new URIImpl( appString + jobsString );
		RDFResource jobsResource = new RDFResource( containerURI );
		DirectContainer jobsContainer = DirectContainerFactory.getInstance().create( jobsResource, app.getURI(), AppDescription.Property.JOB.getURI() );
		jobsContainer.setMemberOfRelation( JobDescription.Property.APP_RELATED.getURI() );
		sourceRepository.createAccessPoint( app.getURI(), jobsContainer );
		aclRepository.createACL( jobsContainer.getURI() );
	}

	private void createJobsQueue( App app ) {
		URI appJobsExecutionQueue = new URIImpl( app.getURI().stringValue() + Consts.HASH_SIGN + Vars.getInstance().getJobsExecutionQueue() );
		app.setJobsExecutionQueue( appJobsExecutionQueue );
		app.getBaseModel().add( appJobsExecutionQueue, RDF.FIRST, appJobsExecutionQueue, app.getURI() );
		app.getBaseModel().add( appJobsExecutionQueue, RDF.REST, RDF.NIL, app.getURI() );
		documentRepository.set( app.getURI(), app.getDocument() );
	}
}

