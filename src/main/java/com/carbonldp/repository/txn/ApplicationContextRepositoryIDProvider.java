package com.carbonldp.repository.txn;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContextHolder;
import org.eclipse.rdf4j.spring.DynamicRepositoryManagerConnectionFactory.RepositoryIdProvider;

public class ApplicationContextRepositoryIDProvider implements RepositoryIdProvider {

	@Override
	public String getRepositoryId() {
		App application = AppContextHolder.getContext().getApplication();

		if ( application == null ) return null;
		else return application.getRepositoryID();
	}
}