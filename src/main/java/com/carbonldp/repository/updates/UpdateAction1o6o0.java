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
import com.carbonldp.rdf.RDFListFactory;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.IRI;
import org.openrdf.model.vocabulary.RDF;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
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
		String appString = app.getIRI().stringValue();
		String backupsString = Vars.getInstance().getBackupsContainer();
		IRI containerIRI = valueFactory.createIRI( appString + backupsString );
		RDFResource backupsResource = new RDFResource( containerIRI );
		BasicContainer backupsContainer = BasicContainerFactory.getInstance().create( backupsResource );
		containerRepository.createChild( app.getIRI(), backupsContainer );
		aclRepository.createACL( backupsContainer.getIRI() );
	}

	private void createJobsContainer( App app ) {
		String appString = app.getIRI().stringValue();
		String jobsString = Vars.getInstance().getJobsContainer();
		IRI containerIRI = valueFactory.createIRI( appString + jobsString );
		RDFResource jobsResource = new RDFResource( containerIRI );
		BasicContainer jobsContainer = BasicContainerFactory.getInstance().create( jobsResource, valueFactory.createIRI( LDP.Properties.MEMBER ), JobDescription.Property.EXECUTION_QUEUE_LOCATION.getIRI() );
		RDFListFactory.getInstance().createQueue( jobsContainer );

		containerRepository.createChild( app.getIRI(), jobsContainer );
		aclRepository.createACL( jobsContainer.getIRI() );
	}
}
