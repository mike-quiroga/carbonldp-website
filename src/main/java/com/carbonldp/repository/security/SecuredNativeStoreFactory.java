package com.carbonldp.repository.security;

import org.openrdf.sail.Sail;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreFactory;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class SecuredNativeStoreFactory extends NativeStoreFactory {
	public static final String SAIL_TYPE = "carbon:Store";

	@Override
	public Sail getSail( SailImplConfig config ) throws SailConfigException {
		if ( ! SAIL_TYPE.equals( config.getType() ) ) {
			throw new SailConfigException( "Invalid Sail type: " + config.getType() );
		}

		SecuredNativeStore securedNativeStore = new SecuredNativeStore();

		if ( config instanceof SecuredNativeStoreConfig ) {
			SecuredNativeStoreConfig nativeConfig = (SecuredNativeStoreConfig) config;

			securedNativeStore.setTripleIndexes( nativeConfig.getTripleIndexes() );
			securedNativeStore.setForceSync( nativeConfig.getForceSync() );
			securedNativeStore.setFederatedServiceResolver( getFederatedServiceResolver() );

			if ( nativeConfig.getValueCacheSize() >= 0 ) {
				securedNativeStore.setValueCacheSize( nativeConfig.getValueCacheSize() );
			}
			if ( nativeConfig.getValueIDCacheSize() >= 0 ) {
				securedNativeStore.setValueIDCacheSize( nativeConfig.getValueIDCacheSize() );
			}
			if ( nativeConfig.getNamespaceCacheSize() >= 0 ) {
				securedNativeStore.setNamespaceCacheSize( nativeConfig.getNamespaceCacheSize() );
			}
			if ( nativeConfig.getNamespaceIDCacheSize() >= 0 ) {
				securedNativeStore.setNamespaceIDCacheSize( nativeConfig.getNamespaceIDCacheSize() );
			}
		}

		return securedNativeStore;
	}

	@Override
	public String getSailType() {
		return SAIL_TYPE;
	}
}
