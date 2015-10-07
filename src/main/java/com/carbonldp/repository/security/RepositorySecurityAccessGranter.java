package com.carbonldp.repository.security;

import org.openrdf.model.Statement;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public interface RepositorySecurityAccessGranter {
	public Vote canAccess( Statement statement );

	public enum Vote {
		GRANT,
		ABSTAIN,
		DENY
	}
}
