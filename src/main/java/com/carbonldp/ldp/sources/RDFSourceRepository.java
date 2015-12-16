package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

import java.util.Collection;
import java.util.Set;

public interface RDFSourceRepository {
	public boolean exists( URI sourceURI );

	boolean is( URI resourceURI, RDFNodeEnum type );

	public RDFSource get( URI sourceURI );

	public Set<RDFSource> get( Set<URI> sourceURIs );

	public URI getDefaultInteractionModel( URI targetURI );

	public DateTime getModified( URI sourceURI );

	public DateTime touch( URI sourceURI );

	public DateTime touch( URI sourceURI, DateTime modified );

	public void add( URI sourceURI, Collection<RDFResource> statementsToAdd );

	public void createAccessPoint( URI sourceURI, AccessPoint accessPoint );

	public void update( RDFSource source );

	public void subtract( URI sourceURI, Collection<RDFResource> statementsToDelete );

	public void delete( URI sourceURI );

	public void deleteOccurrences( URI sourceURI, boolean includeChildren );

	public void replace( RDFSource source );

	public void set( URI sourceURI, Collection<RDFResource> resourceViews );
}
