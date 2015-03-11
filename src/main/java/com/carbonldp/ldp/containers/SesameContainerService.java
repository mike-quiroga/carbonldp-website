package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
public class SesameContainerService extends AbstractSesameLDPService implements ContainerService {
	public SesameContainerService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( sourceRepository, containerRepository );
	}

	@Override
	public Container get( URI containerURI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences ) {
		return containerRepository.get( containerURI, containerRetrievalPreferences );
	}

	@Override
	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( URI containerURI ) {
		return containerRepository.getRetrievalPreferences( containerURI );
	}

	@Override
	public DateTime createChild( URI containerURI, BasicContainer basicContainer ) {
		DateTime creationTime = DateTime.now();

		basicContainer.setTimestamps( creationTime );
		containerRepository.createChild( containerURI, basicContainer );
		sourceRepository.touch( containerURI, creationTime );

		return creationTime;
	}
}
