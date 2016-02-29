package com.carbonldp.sparql;

import com.carbonldp.repository.security.RepositorySecuritySwitch;
import com.carbonldp.utils.Action;
import com.carbonldp.utils.ActionWithResult;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public final class SecuredRepositoryTemplate {
	private SecuredRepositoryTemplate() {}

	public static <E> E execute( ActionWithResult<E> action ) {
		RepositorySecuritySwitch.enable();

		try {
			return action.run();
		} finally {
			RepositorySecuritySwitch.disable();
		}
	}

	public static void execute( Action action ) {
		RepositorySecuritySwitch.enable();

		try {
			action.run();
		} finally {
			RepositorySecuritySwitch.disable();
		}
	}
}
