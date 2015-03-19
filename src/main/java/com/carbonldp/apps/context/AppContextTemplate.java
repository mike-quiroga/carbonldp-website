package com.carbonldp.apps.context;

import com.carbonldp.apps.App;
import com.carbonldp.utils.Action;

public final class AppContextTemplate {
	private AppContextTemplate() {
		// Meaning non-instantiable
	}

	public static void runInAppContext( App app, Action action ) {
		changeAppContext( app );
		try {
			action.run();
		} catch ( Exception e ) {
			if ( ! ( e instanceof RuntimeException ) ) throw new RuntimeException( e );
			else throw (RuntimeException) e;
		} finally {
			restoreAppContext();
		}
	}

	private static void changeAppContext( App app ) {
		AppContext originalContext = AppContextHolder.getContext();
		AppContext applicationContext = new TemporaryAppContext( originalContext );
		applicationContext.setApplication( app );
		AppContextHolder.setContext( applicationContext );
	}

	private static void restoreAppContext() {
		AppContext currentContext = AppContextHolder.getContext();
		if ( ! ( currentContext instanceof TemporaryAppContext ) ) {
			throw new IllegalStateException( "The authentication has changed during the method call." );
		}
		AppContext originalContext = ( (TemporaryAppContext) currentContext ).getOriginalContext();
		AppContextHolder.setContext( originalContext );
	}
}
