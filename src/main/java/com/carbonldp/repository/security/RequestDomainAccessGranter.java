package com.carbonldp.repository.security;

import com.carbonldp.utils.RequestUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class RequestDomainAccessGranter implements RepositorySecurityAccessGranter {

	@Override
	public Vote canAccess( Statement statement ) {
		Resource contextResource = statement.getContext();
		if ( contextResource == null || ! ValueUtil.isURI( contextResource ) ) return Vote.ABSTAIN;

		String contextURI = ValueUtil.getURI( contextResource ).stringValue();
		String requestURI = RequestUtil.getRequestURL();
		if ( requestURI == null ) return Vote.ABSTAIN;

		if ( URIUtil.hasBase( contextURI, requestURI ) ) return Vote.ABSTAIN;
		else return Vote.DENY;
	}
}
