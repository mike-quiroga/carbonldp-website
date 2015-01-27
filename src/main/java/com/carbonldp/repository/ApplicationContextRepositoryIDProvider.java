package com.carbonldp.repository;

import org.openrdf.spring.DynamicRepositoryManagerConnectionFactory.RepositoryIdProvider;

import com.carbonldp.apps.Application;
import com.carbonldp.apps.context.ApplicationContextHolder;

public class ApplicationContextRepositoryIDProvider implements RepositoryIdProvider {

	@Override
	public String getRepositoryId() {
		Application application = ApplicationContextHolder.getContext().getApplication();

		if ( application == null ) return null;
		else return application.getRepositoryID();
	}
}