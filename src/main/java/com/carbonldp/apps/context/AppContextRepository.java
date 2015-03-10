package com.carbonldp.apps.context;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.RunWith;
import org.openrdf.model.URI;

public class AppContextRepository {
	private final AppRepository appRepository;

	public AppContextRepository( AppRepository appRepository ) {
		this.appRepository = appRepository;
	}

	@RunWith( platformRoles = Platform.Role.SYSTEM )
	public App getApp( URI rootContainerURI ) {
		return appRepository.findByRootContainer( rootContainerURI );
	}
}
