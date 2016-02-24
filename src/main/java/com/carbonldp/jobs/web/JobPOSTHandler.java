package com.carbonldp.jobs.web;

import com.carbonldp.jobs.BackupJobFactory;
import com.carbonldp.jobs.Job;
import com.carbonldp.jobs.JobFactory;
import com.carbonldp.jobs.JobService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class JobPOSTHandler extends AbstractRDFPostRequestHandler<Job> {
	protected JobService jobService;

	@Override
	protected Job getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return BackupJobFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, Job documentResourceView ) {
		jobService.create( targetURI, documentResourceView );
	}

	@Autowired
	public void setJobService( JobService jobService ) { this.jobService = jobService; }
}
