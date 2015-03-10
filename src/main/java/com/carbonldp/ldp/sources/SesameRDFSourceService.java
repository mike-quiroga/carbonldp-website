package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.models.RDFResource;
import com.carbonldp.models.RDFSource;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	public SesameRDFSourceService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( sourceRepository, containerRepository );
	}

	@Override
	public boolean exists( URI targetURI ) {
		return false;
	}

	@Override
	public RDFSource get( URI targetURI ) {
		return null;
	}

	@Override
	public DateTime getModified( URI uri ) {
		return null;
	}

	@Override
	public URI getDefaultInteractionModel( URI targetURI ) {
		return null;
	}

	@Override
	public void createAccessPoint( URI parentURI, RDFResource accessPoint ) {

	}

	@Override
	public void touch( URI targetURI, DateTime now ) {

	}
}
