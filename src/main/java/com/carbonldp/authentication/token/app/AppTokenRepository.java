package com.carbonldp.authentication.token.app;

import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since 0.15.0_ALPHA
 */
public interface AppTokenRepository {
	public Container createAppTokensContainer( URI rootContainerURI );
}
