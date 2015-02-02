package com.carbonldp.apps.context;

public interface ApplicationContextHolderStrategy {
	public void clearContext();

	public ApplicationContext getContext();

	public void setContext(ApplicationContext context);

	public ApplicationContext createEmptyContext();
}
