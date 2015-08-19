package com.carbonldp.apps.context;

import org.springframework.util.Assert;

/**
 * A <code>ThreadLocal</code>-based implementation of {@link AppContextHolderStrategy}.
 *
 * @author MiguelAraCo
 * @see java.lang.ThreadLocal
 */
public final class ThreadLocalAppContextHolderStrategy implements AppContextHolderStrategy {
	private static final ThreadLocal<AppContext> contextHolder = new ThreadLocal<AppContext>();

	public void clearContext() {
		contextHolder.remove();
	}

	public AppContext getContext() {
		AppContext context = contextHolder.get();

		if ( context == null ) context = createEmptyContext();

		return context;
	}

	public void setContext( AppContext context ) {
		Assert.notNull( context, "Only non-null ApplicationContext instances are permitted" );
		contextHolder.set( context );
	}

	public AppContext createEmptyContext() {
		AppContext context = new AppContextImpl();
		contextHolder.set( context );
		return context;
	}
}
