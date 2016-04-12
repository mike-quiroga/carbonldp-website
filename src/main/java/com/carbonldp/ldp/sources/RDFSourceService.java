package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFDocument;
import org.joda.time.DateTime;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RDFSourceService {
	public boolean exists( IRI sourceIRI );

	@PreAuthorize( "hasPermission(#sourceIRI, 'READ')" )
	public RDFSource get( IRI sourceIRI );

	public String getETag( IRI sourceIRI );

	public DateTime getModified( IRI sourceIRI );

	@PreAuthorize( "hasPermission(#sourceIRI, 'READ')" )
	public IRI getDefaultInteractionModel( IRI sourceIRI );

	@PreAuthorize( "hasPermission(#parentIRI, 'CREATE_ACCESS_POINT')" )
	public DateTime createAccessPoint( IRI parentIRI, AccessPoint accessPoint );

	@PreAuthorize( "hasPermission(#sourceIRI, 'UPDATE')" )
	public void touch( IRI sourceIRI, DateTime now );

	@PreAuthorize( "hasPermission(#sourceIRI, 'UPDATE')" )
	public void add( IRI sourceIRI, RDFDocument document );

	@PreAuthorize( "hasPermission(#sourceIRI, 'UPDATE')" )
	public void set( IRI sourceIRI, RDFDocument document );

	@PreAuthorize( "hasPermission(#source, 'UPDATE')" )
	public DateTime replace( RDFSource source );

	@PreAuthorize( "hasPermission(#sourceIRI, 'UPDATE')" )
	public void subtract( IRI sourceIRI, RDFDocument document );

	@PreAuthorize( "hasPermission(#sourceIRI, 'DELETE')" )
	public void delete( IRI sourceIRI );

}
