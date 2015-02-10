package com.carbonldp.repository;

import java.util.Set;

import org.openrdf.model.URI;

public interface RDFResourceRepository {
	public Set<URI> getTypes(URI resourceURI);
}
