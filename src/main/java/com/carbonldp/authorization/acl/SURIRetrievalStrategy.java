package com.carbonldp.authorization.acl;

import java.util.List;

import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;

public final class SURIRetrievalStrategy {
	private SURIRetrievalStrategy() {
		// Meaning non-instantiable
	}

	public static final List<URI> getSURIs(Authentication authentication) {
		// TODO
		return null;
	}
}
