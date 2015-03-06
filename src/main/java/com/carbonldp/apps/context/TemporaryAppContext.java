package com.carbonldp.apps.context;

public class TemporaryAppContext extends AppContextImpl implements AppContext {

	private AppContext originalContext;

	public TemporaryAppContext( AppContext originalContext ) {
		this.originalContext = originalContext;
	}

	public AppContext getOriginalContext() {
		return originalContext;
	}

	public void setOriginalContext( AppContext originalContext ) {
		this.originalContext = originalContext;
	}

}
