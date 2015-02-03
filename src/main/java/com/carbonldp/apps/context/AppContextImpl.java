package com.carbonldp.apps.context;

import com.carbonldp.apps.Application;

public class AppContextImpl implements AppContext {
	private Application application;

	public Application getApplication() {
		return this.application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public boolean isEmpty() {
		return this.application == null;
	}

	@Override
	public int hashCode() {
		if ( this.application == null ) {
			return - 1;
		} else {
			return this.application.hashCode();
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(super.toString());

		if ( isEmpty() ) {
			stringBuilder.append(": Empty ApplicationContext");
		} else {
			stringBuilder.append(": Authentication: ").append(this.application);
		}

		return stringBuilder.toString();
	}
}
