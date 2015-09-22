package org.openrdf.sail.nativerdf;

import org.openrdf.sail.nativerdf.config.NativeStoreConfig;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class CarbonStoreConfig extends NativeStoreConfig {
	public CarbonStoreConfig() {
		setType( CarbonStoreFactory.SAIL_TYPE );
	}

	public CarbonStoreConfig( String tripleIndexes ) {
		this();
		setTripleIndexes( tripleIndexes );
	}

	public CarbonStoreConfig( String tripleIndexes, boolean forceSync ) {
		this( tripleIndexes );
		setForceSync( forceSync );
	}
}
