package com.carbonldp.repository.security;

import org.openrdf.sail.nativerdf.config.NativeStoreConfig;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class SecuredNativeStoreConfig extends NativeStoreConfig {
	public SecuredNativeStoreConfig() {
		setType( SecuredNativeStoreFactory.SAIL_TYPE );
	}

	public SecuredNativeStoreConfig( String tripleIndexes ) {
		this();
		setTripleIndexes( tripleIndexes );
	}

	public SecuredNativeStoreConfig( String tripleIndexes, boolean forceSync ) {
		this( tripleIndexes );
		setForceSync( forceSync );
	}
}
