package org.openrdf.sail.nativerdf;

import org.openrdf.sail.Sail;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreFactory;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class CarbonStoreFactory extends NativeStoreFactory {
	public static final String SAIL_TYPE = "carbon:Store";

	@Override
	public Sail getSail( SailImplConfig config ) throws SailConfigException {
		if ( ! SAIL_TYPE.equals( config.getType() ) ) {
			throw new SailConfigException( "Invalid Sail type: " + config.getType() );
		}

		CarbonStore carbonStore = new CarbonStore();

		if ( config instanceof CarbonStoreConfig ) {
			CarbonStoreConfig nativeConfig = (CarbonStoreConfig) config;

			carbonStore.setTripleIndexes( nativeConfig.getTripleIndexes() );
			carbonStore.setForceSync( nativeConfig.getForceSync() );
			carbonStore.setFederatedServiceResolver( getFederatedServiceResolver() );

			if ( nativeConfig.getValueCacheSize() >= 0 ) {
				carbonStore.setValueCacheSize( nativeConfig.getValueCacheSize() );
			}
			if ( nativeConfig.getValueIDCacheSize() >= 0 ) {
				carbonStore.setValueIDCacheSize( nativeConfig.getValueIDCacheSize() );
			}
			if ( nativeConfig.getNamespaceCacheSize() >= 0 ) {
				carbonStore.setNamespaceCacheSize( nativeConfig.getNamespaceCacheSize() );
			}
			if ( nativeConfig.getNamespaceIDCacheSize() >= 0 ) {
				carbonStore.setNamespaceIDCacheSize( nativeConfig.getNamespaceIDCacheSize() );
			}
		}

		return carbonStore;
	}

	@Override
	public String getSailType() {
		return SAIL_TYPE;
	}
}
