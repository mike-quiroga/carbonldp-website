package com.carbonldp.jobs.web;

import com.carbonldp.jobs.Job;
import com.carbonldp.jobs.JobFactory;
import com.carbonldp.jobs.JobService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class JobsPOSTHandler extends AbstractRDFPostRequestHandler<Job> {
	protected JobService jobService;

	@Override
	protected Job getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return JobFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( IRI targetIRI, Job documentResourceView ) {
		jobService.create( targetIRI, documentResourceView );
	}

	@Autowired
	public void setJobService( JobService jobService ) { this.jobService = jobService; }
}
