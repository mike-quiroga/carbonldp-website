package com.carbonldp.apps.context;

import com.carbonldp.apps.App;

public interface AppContext {
	public App getApplication();

	public void setApplication( App application );

	public boolean isEmpty();
}
