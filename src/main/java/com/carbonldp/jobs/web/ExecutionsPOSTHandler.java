package com.carbonldp.jobs.web;

import com.carbonldp.jobs.Execution;
import com.carbonldp.jobs.ExecutionFactory;
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
public class ExecutionsPOSTHandler extends AbstractRDFPostRequestHandler<Execution> {
	JobService jobService;

	@Override
	protected Execution getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return ExecutionFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( IRI targetIRI, Execution documentResourceView ) {
		jobService.createExecution( targetIRI, documentResourceView );
	}

	@Autowired
	public void setJobService( JobService jobService ) {
		this.jobService = jobService;
	}
}