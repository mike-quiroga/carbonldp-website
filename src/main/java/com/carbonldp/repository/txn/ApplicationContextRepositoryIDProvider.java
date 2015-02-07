package com.carbonldp.repository.txn;

import org.openrdf.spring.DynamicRepositoryManagerConnectionFactory.RepositoryIdProvider;

import com.carbonldp.apps.Application;
import com.carbonldp.apps.context.AppContextHolder;

public class ApplicationContextRepositoryIDProvider implements RepositoryIdProvider {

	@Override
	public String getRepositoryId() {
		Application application = AppContextHolder.getContext().getApplication();

		if ( application == null ) return null;
		else return application.getRepositoryID();
	}
}