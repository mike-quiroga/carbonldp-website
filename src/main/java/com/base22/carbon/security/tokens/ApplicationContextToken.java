package com.base22.carbon.security.tokens;

import com.base22.carbon.security.models.Application;

public interface ApplicationContextToken {
	public Application getCurrentApplicationContext();

	public void setCurrentApplicationContext(Application application);
}
