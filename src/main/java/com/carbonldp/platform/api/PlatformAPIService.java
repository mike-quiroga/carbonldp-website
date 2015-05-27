package com.carbonldp.platform.api;

import com.carbonldp.AbstractComponent;

public class PlatformAPIService extends AbstractComponent {

	private PlatformAPIRepository apiRepository;

	public PlatformAPIService( PlatformAPIRepository apiRepository ) {
		this.apiRepository = apiRepository;
	}

	public PlatformAPI get() {
		return apiRepository.get();
	}
}
