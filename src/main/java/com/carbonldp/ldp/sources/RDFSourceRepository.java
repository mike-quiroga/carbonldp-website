package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFNodeEnum;
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

	public void add( URI sourceURI, RDFDocument document );

	public void createAccessPoint( URI sourceURI, AccessPoint accessPoint );

	public void update( RDFSource source );

	public void subtract( URI sourceURI, RDFDocument document );

	public void delete( URI sourceURI );

	public void deleteOccurrences( URI sourceURI, boolean includeChildren );

	public void replace( RDFSource source );

	public void set( URI sourceURI, RDFDocument document );

	boolean is( URI resourceURI, RDFNodeEnum type );
}
