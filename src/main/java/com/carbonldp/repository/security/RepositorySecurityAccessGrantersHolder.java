package com.carbonldp.repository.security;

import java.util.*;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public final class RepositorySecurityAccessGrantersHolder {

	private static RepositorySecurityAccessGrantersHolder instance;

	public static RepositorySecurityAccessGrantersHolder getInstance() {
		if ( instance == null ) instance = new RepositorySecurityAccessGrantersHolder();
		return instance;
	}

	private final List<RepositorySecurityAccessGranter> accessGranters;

	public RepositorySecurityAccessGrantersHolder( RepositorySecurityAccessGranter... granters ) {
		if ( granters == null || granters.length == 0 ) granters = new RepositorySecurityAccessGranter[0];

		this.accessGranters = Collections.unmodifiableList( Arrays.asList( granters ) );
		RepositorySecurityAccessGrantersHolder.instance = this;
	}

	public List<RepositorySecurityAccessGranter> getAccessGranters() {
		return accessGranters;
	}
}