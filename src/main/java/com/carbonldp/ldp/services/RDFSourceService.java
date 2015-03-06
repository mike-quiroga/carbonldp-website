package com.carbonldp.ldp.services;

import com.carbonldp.models.AccessPoint;
import com.carbonldp.models.RDFSource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface RDFSourceService {
	public boolean exists(URI sourceURI);

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public RDFSource get(URI sourceURI);

	public Set<RDFSource> get(Set<URI> sourceURIs);

	public URI getDefaultInteractionModel(URI targetURI);

	public DateTime getModified(URI sourceURI);

	public DateTime touch(URI sourceURI);

	public DateTime touch(URI sourceURI, DateTime modified);

	public void createAccessPoint(URI sourceURI, AccessPoint accessPoint);

	public void update(RDFSource source);

	public void delete(URI sourceURI);
}
