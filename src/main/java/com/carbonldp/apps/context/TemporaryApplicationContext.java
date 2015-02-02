package com.carbonldp.apps.context;

public class TemporaryApplicationContext extends ApplicationContextImpl implements ApplicationContext {

	private ApplicationContext originalContext;

	public TemporaryApplicationContext(ApplicationContext originalContext) {
		this.originalContext = originalContext;
	}

	public ApplicationContext getOriginalContext() {
		return originalContext;
	}

	public void setOriginalContext(ApplicationContext originalContext) {
		this.originalContext = originalContext;
	}

}
