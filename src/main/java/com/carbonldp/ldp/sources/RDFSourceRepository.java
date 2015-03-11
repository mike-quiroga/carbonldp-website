package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

import java.util.Set;

public interface RDFSourceRepository {
	public boolean exists( URI sourceURI );

	public RDFSource get( URI sourceURI );

	public Set<RDFSource> get( Set<URI> sourceURIs );

	public URI getDefaultInteractionModel( URI targetURI );

	public DateTime getModified( URI sourceURI );

	public DateTime touch( URI sourceURI );

	public DateTime touch( URI sourceURI, DateTime modified );

	public void createAccessPoint( URI sourceURI, AccessPoint accessPoint );

	public void update( RDFSource source );

	public void delete( URI sourceURI );
}
