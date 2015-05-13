package com.carbonldp.agents.validators;

import com.carbonldp.ldp.web.AbstractGETRequestHandler;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;

@RequestHandler
public class AgentValidatorGETHandler extends AbstractGETRequestHandler {
	@Override
	protected ResponseEntity<Object> handleRDFSourceRetrieval( URI targetURI ) {
		throw new NotImplementedException();
	}
}
