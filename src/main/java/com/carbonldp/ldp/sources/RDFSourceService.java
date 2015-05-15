package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFResource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;

public interface RDFSourceService {
	// @PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	boolean exists( URI sourceURI );

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	RDFSource get( URI sourceURI );

	// @PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	DateTime getModified( URI sourceURI );

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	URI getDefaultInteractionModel( URI sourceURI );

	@PreAuthorize( "hasPermission(#parentURI, 'CREATE_ACCESS_POINT')" )
	DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	void touch( URI sourceURI, DateTime now );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	void add( URI sourceURI, Collection<RDFResource> resourceViews );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	void set( URI sourceURI, Collection<RDFResource> resourceViews );

	@PreAuthorize( "hasPermission(#source, 'UPDATE')" )
	DateTime replace( RDFSource source );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	void substract( URI sourceURI, Collection<RDFResource> resourceViews );

	@PreAuthorize( "hasPermission(#sourceURI, 'DELETE')" )
	void delete( URI sourceURI );
}
