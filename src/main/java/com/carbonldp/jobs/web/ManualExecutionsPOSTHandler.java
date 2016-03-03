package com.carbonldp.jobs.web;

import com.carbonldp.jobs.Execution;
import com.carbonldp.jobs.ExecutionFactory;
import com.carbonldp.jobs.ExecutionService;
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
public class ManualExecutionsPOSTHandler extends AbstractRDFPostRequestHandler<Execution> {
	ExecutionService executionService;

	@Override
	protected Execution getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return ExecutionFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, Execution documentResourceView ) {
		executionService.createChild( targetURI, documentResourceView );
	}

	@Autowired
	public void setExecutionService( ExecutionService executionService ) {this.executionService = executionService; }
}