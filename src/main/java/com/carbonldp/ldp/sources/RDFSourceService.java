package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFResource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;

public interface RDFSourceService {
	public boolean exists( URI sourceURI );

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public RDFSource get( URI sourceURI );

	public DateTime getModified( URI sourceURI );

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public URI getDefaultInteractionModel( URI sourceURI );

	@PreAuthorize( "hasPermission(#parentURI, 'CREATE_ACCESS_POINT')" )
	public DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void touch( URI sourceURI, DateTime now );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void add( URI sourceURI, Collection<RDFResource> resourceViews );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void set( URI sourceURI, Collection<RDFResource> resourceViews );

	@PreAuthorize( "hasPermission(#source, 'UPDATE')" )
	public DateTime replace( RDFSource source );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void substract( URI sourceURI, Collection<RDFResource> resourceViews );

	@PreAuthorize( "hasPermission(#sourceURI, 'DELETE')" )
	public void delete( URI sourceURI );

}
