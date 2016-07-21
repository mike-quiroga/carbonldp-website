package com.carbonldp.repository.security;

import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RequestUtil;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class RequestDomainAccessGranter implements RepositorySecurityAccessGranter {

	@Override
	public Vote canAccess( Statement statement ) {
		Resource contextResource = statement.getContext();
		if ( contextResource == null || ! ValueUtil.isIRI( contextResource ) ) return Vote.ABSTAIN;

		String contextIRI = ValueUtil.getIRI( contextResource ).stringValue();
		String requestIRI = RequestUtil.getRequestURL();
		if ( requestIRI == null ) return Vote.ABSTAIN;

		if ( IRIUtil.hasBase( contextIRI, requestIRI ) ) return Vote.ABSTAIN;
		else return Vote.DENY;
	}
}
