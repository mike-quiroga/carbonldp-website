package com.carbonldp.ldp.sources;

import com.carbonldp.models.AccessPoint;
import com.carbonldp.models.RDFSource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

public interface RDFSourceService {
	// @PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	boolean exists( URI sourceURI );

	// @PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public RDFSource get( URI sourceURI );

	// @PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public DateTime getModified( URI uri );

	// @PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public URI getDefaultInteractionModel( URI sourceURI );

	// @PreAuthorize( "hasPermission(#parentURI, 'CREATE_ACCESS_POINT')" )
	public DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint );

	// @PreAuthorize( "hasPermission(#parentURI, 'UPDATE')" )
	public void touch( URI sourceURI, DateTime now );
}
