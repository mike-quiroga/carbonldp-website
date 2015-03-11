package com.carbonldp.apps;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;

public class SesameAppService extends AbstractSesameLDPService implements AppService {
	private final AppRepository appRepository;

	public SesameAppService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository, AppRepository appRepository ) {
		super( sourceRepository, containerRepository );
		this.appRepository = appRepository;
	}

	@Override
	public App create( App app ) {
		app = appRepository.create( app );
		appRepository.initialize( app );

		return app;
	}
}
