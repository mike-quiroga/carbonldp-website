package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppDescription;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.jobs.JobDescription;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o5o0 extends AbstractUpdateAction {

	protected RDFSourceRepository sourceRepository;
	protected ACLRepository aclRepository;

	@Override
	public void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			createBackupContainer( app );
			createJobsContainer( app );
		}
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

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) { this.sourceRepository = sourceRepository; }

	@Autowired
	public void setAclRepository( ACLRepository aclRepository ) { this.aclRepository = aclRepository; }
}

