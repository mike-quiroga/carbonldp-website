package com.carbonldp.apps.context;

import com.carbonldp.apps.Application;

public interface AppContext {
	public Application getApplication();

	public void setApplication(Application application);

	public boolean isEmpty();
}
