package com.carbonldp.apps.context;

public interface AppContextHolderStrategy {
	public void clearContext();

	public AppContext getContext();

	public void setContext( AppContext context );

	public AppContext createEmptyContext();
}
