package com.base22.carbon.authentication;

import com.base22.carbon.apps.Application;

public interface ApplicationContextToken {
	public Application getCurrentApplicationContext();

	public void setCurrentApplicationContext(Application application);
}
