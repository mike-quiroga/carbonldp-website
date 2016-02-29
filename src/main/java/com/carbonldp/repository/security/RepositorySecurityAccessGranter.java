package com.carbonldp.repository.security;

import org.openrdf.model.Statement;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public interface RepositorySecurityAccessGranter {
	public Vote canAccess( Statement statement );

	public enum Vote {
		GRANT,
		ABSTAIN,
		DENY
	}
}
