package com.carbonldp.platform.api;

import com.carbonldp.AbstractComponent;
import org.springframework.beans.factory.annotation.Autowired;

public class PlatformAPIService extends AbstractComponent {

	protected PlatformAPIRepository apiRepository;

	public PlatformAPI get() {
		return apiRepository.get();
	}

	@Autowired
	public void setAPIRepository( PlatformAPIRepository apiRepository ) { this.apiRepository = apiRepository; }
}
