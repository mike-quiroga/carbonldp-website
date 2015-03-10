package com.carbonldp.ldp.sources;

import com.carbonldp.models.RDFResource;
import com.carbonldp.models.RDFSource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

public interface RDFSourceService {
	boolean exists( URI targetURI );

	public RDFSource get( URI targetURI );

	public DateTime getModified( URI uri );

	public URI getDefaultInteractionModel( URI targetURI );

	public void createAccessPoint( URI targetURI, RDFResource requestAccessPoint );

	public void touch( URI targetURI, DateTime now );
}
