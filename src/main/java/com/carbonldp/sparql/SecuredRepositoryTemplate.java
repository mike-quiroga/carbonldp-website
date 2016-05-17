package com.carbonldp.sparql;

import com.carbonldp.repository.security.RepositorySecurityAccessGranter;
import com.carbonldp.repository.security.RepositorySecurityAccessGrantersHolder;
import com.carbonldp.repository.security.RepositorySecuritySwitch;
import com.carbonldp.utils.Action;
import com.carbonldp.utils.ActionWithResult;
import com.google.common.base.Function;

import java.util.function.Consumer;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public final class SecuredRepositoryTemplate {
	private SecuredRepositoryTemplate() {
		RepositorySecuritySwitch.enable();
	}

	public static <E> E execute( ActionWithResult<E> action ) {
		RepositorySecuritySwitch.enable();
		try {
			return action.run();
		} finally {
			SecuredRepositoryTemplate.clear();
		}
	}

	public static <E> E execute( Function<SecuredRepositoryTemplate, E> action ) {
		try {
			return action.apply( new SecuredRepositoryTemplate() );
		} finally {
			SecuredRepositoryTemplate.clear();
		}
	}

	public static void execute( Action action ) {
		RepositorySecuritySwitch.enable();
		try {
			action.run();
		} finally {
			SecuredRepositoryTemplate.clear();
		}
	}

	public static void execute( Consumer<SecuredRepositoryTemplate> action ) {
		try {
			action.accept( new SecuredRepositoryTemplate() );
		} finally {
			SecuredRepositoryTemplate.clear();
		}
	}

	public void setFirstAccessGranters( RepositorySecurityAccessGranter... accessGranters ) {
		RepositorySecurityAccessGrantersHolder.setFirst( accessGranters );
	}

	public void setLastAccessGranters( RepositorySecurityAccessGranter... accessGranters ) {
		RepositorySecurityAccessGrantersHolder.setLast( accessGranters );
	}

	private static void clear() {
		RepositorySecuritySwitch.disable();
		RepositorySecurityAccessGrantersHolder.clear();
	}
}
