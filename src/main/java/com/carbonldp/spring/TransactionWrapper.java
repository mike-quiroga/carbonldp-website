package com.carbonldp.spring;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.utils.Action;
import com.carbonldp.utils.ActionWithResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional( propagation = Propagation.REQUIRES_NEW )
public class TransactionWrapper {
	public void run( Action action ) {
		action.run();
	}

	public <E> E run( ActionWithResult<E> action ) {
		return action.run();
	}

	@RunInPlatformContext
	public void runInPlatformContext( Action action ) {
		action.run();
	}

	@RunInPlatformContext
	public <E> E runInPlatformContext( ActionWithResult<E> action ) {
		return action.run();
	}

	@RunInAppContext
	public void runInAppcontext( App app, Action action ) {
		action.run();
	}

	@RunInAppContext
	public <E> E runInAppcontext( App app, ActionWithResult<E> action ) {
		return action.run();
	}

	@RunWith( platformRoles = Platform.Role.SYSTEM )
	@RunInAppContext
	public <E> E runWithSystemPermissionsInAppContext( App app, ActionWithResult<E> action ) {
		return action.run();
	}

}
