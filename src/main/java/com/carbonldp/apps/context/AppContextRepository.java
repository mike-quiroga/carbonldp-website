package com.carbonldp.apps.context;

import org.openrdf.model.URI;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppService;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.spring.Inject;

public class AppContextRepository {
	private AppService appService;

	@RunWith(platformRoles = Platform.Role.SYSTEM)
	public App getApp(URI rootContainerURI) {
		return appService.findByRootContainer(rootContainerURI);
	}

	@Inject
	public void setAppService(AppService appService) {
		this.appService = appService;
	}
}
