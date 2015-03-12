package com.carbonldp.ldp.sources;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.ldp.containers.ContainerRepository;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	public SesameRDFSourceService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( sourceRepository, containerRepository, aclRepository );
	}

	@Override
	public boolean exists( URI sourceURI ) {
		return sourceRepository.exists( sourceURI );
	}

	@Override
	public RDFSource get( URI sourceURI ) {
		return sourceRepository.get( sourceURI );
	}

	@Override
	public DateTime getModified( URI sourceURI ) {
		return sourceRepository.getModified( sourceURI );
	}

	@Override
	public URI getDefaultInteractionModel( URI sourceURI ) {
		return sourceRepository.getDefaultInteractionModel( sourceURI );
	}

	@Override
	public DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint ) {
		// TODO: Move controller validation here
		DateTime creationTime = DateTime.now();

		accessPoint.setTimestamps( creationTime );
		sourceRepository.createAccessPoint( parentURI, accessPoint );
		sourceRepository.touch( parentURI, creationTime );

		return creationTime;
	}

	@Override
	public void touch( URI sourceURI, DateTime now ) {
		sourceRepository.touch( sourceURI, now );
	}
}
