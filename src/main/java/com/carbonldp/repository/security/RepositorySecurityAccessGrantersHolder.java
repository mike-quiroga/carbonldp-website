package com.carbonldp.repository.security;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public final class RepositorySecurityAccessGrantersHolder {
	private static final ThreadLocal<RepositorySecurityAccessGranter[]> firstAccessGranters = new ThreadLocal<>();
	private static final ThreadLocal<RepositorySecurityAccessGranter[]> lastAccessGranters = new ThreadLocal<>();
	private static RepositorySecurityAccessGrantersHolder instance;

	public static RepositorySecurityAccessGranter[] get() {
		RepositorySecurityAccessGranter[] firstAccessGranters = RepositorySecurityAccessGrantersHolder.firstAccessGranters.get();
		RepositorySecurityAccessGranter[] defaultGranters = RepositorySecurityAccessGrantersHolder.getInstance().defaultAccessGranters;
		RepositorySecurityAccessGranter[] lastAccessGranters = RepositorySecurityAccessGrantersHolder.lastAccessGranters.get();

		return ArrayUtils.addAll( ArrayUtils.addAll( firstAccessGranters, defaultGranters ), lastAccessGranters );
	}

	public static void setFirst( RepositorySecurityAccessGranter... granters ) {
		if ( granters == null || granters.length == 0 ) granters = new RepositorySecurityAccessGranter[0];

		RepositorySecurityAccessGrantersHolder.firstAccessGranters.set( granters.clone() );
	}

	public static void setLast( RepositorySecurityAccessGranter... granters ) {
		if ( granters == null || granters.length == 0 ) granters = new RepositorySecurityAccessGranter[0];

		RepositorySecurityAccessGrantersHolder.lastAccessGranters.set( granters.clone() );
	}

	public static void clear() {
		RepositorySecurityAccessGrantersHolder.firstAccessGranters.remove();
		RepositorySecurityAccessGrantersHolder.lastAccessGranters.remove();
	}

	public static RepositorySecurityAccessGrantersHolder getInstance() {
		if ( instance == null ) instance = new RepositorySecurityAccessGrantersHolder();
		return instance;
	}

	private final RepositorySecurityAccessGranter[] defaultAccessGranters;

	public RepositorySecurityAccessGrantersHolder( RepositorySecurityAccessGranter... granters ) {
		if ( granters == null || granters.length == 0 ) granters = new RepositorySecurityAccessGranter[0];

		this.defaultAccessGranters = granters.clone();
		RepositorySecurityAccessGrantersHolder.instance = this;
	}
}