package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFDocument;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.Set;

public interface RDFSourceService {
	public boolean exists( URI sourceURI );

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public RDFSource get( URI sourceURI );

	@PreFilter( "hasPermission(filterObject, 'READ')" )
	public Set<RDFSource> get(Set<URI> sourceURIs);

	public String getETag( URI sourceURI );

	public DateTime getModified( URI sourceURI );

	@PreAuthorize( "hasPermission(#sourceURI, 'READ')" )
	public URI getDefaultInteractionModel( URI sourceURI );

	@PreAuthorize( "hasPermission(#parentURI, 'CREATE_ACCESS_POINT')" )
	public DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void touch( URI sourceURI, DateTime now );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void add( URI sourceURI, RDFDocument document );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void set( URI sourceURI, RDFDocument document );

	@PreAuthorize( "hasPermission(#source, 'UPDATE')" )
	public DateTime replace( RDFSource source );

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	public void subtract( URI sourceURI, RDFDocument document );

	@PreAuthorize( "hasPermission(#sourceURI, 'DELETE')" )
	public void delete( URI sourceURI );

}
