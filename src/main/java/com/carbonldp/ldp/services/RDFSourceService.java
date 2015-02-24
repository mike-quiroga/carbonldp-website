package com.carbonldp.ldp.services;

import java.util.Set;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import com.carbonldp.models.AccessPoint;
import com.carbonldp.models.RDFSource;

public interface RDFSourceService {
	public boolean exists(URI sourceURI);

	public RDFSource get(URI sourceURI);

	public Set<RDFSource> get(Set<URI> sourceURIs);

	public URI getDefaultInteractionModel(URI targetURI);

	public DateTime touch(URI sourceURI);

	public DateTime touch(URI sourceURI, DateTime modified);

	public void createAccessPoint(URI sourceURI, AccessPoint accessPoint);

	public void update(RDFSource source);

	public void delete(URI sourceURI);
}
