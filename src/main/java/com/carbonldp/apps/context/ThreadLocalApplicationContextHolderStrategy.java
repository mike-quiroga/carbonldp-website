package com.carbonldp.apps.context;

import org.springframework.util.Assert;

/**
 * A <code>ThreadLocal</code>-based implementation of {@link ApplicationContextHolderStrategy}.
 * 
 * @author MiguelAraCo
 *
 * @see java.lang.ThreadLocal
 */
public final class ThreadLocalApplicationContextHolderStrategy implements ApplicationContextHolderStrategy {
	private static final ThreadLocal<ApplicationContext> contextHolder = new ThreadLocal<ApplicationContext>();

	public void clearContext() {
		contextHolder.remove();
	}

	public ApplicationContext getContext() {
		ApplicationContext context = contextHolder.get();

		if ( context == null ) {
			context = createEmptyContext();
			contextHolder.set(context);
		}

		return context;
	}

	public void setContext(ApplicationContext context) {
		Assert.notNull(context, "Only non-null ApplicationContext instances are permitted");
		contextHolder.set(context);
	}

	public ApplicationContext createEmptyContext() {
		return new ApplicationContext();
	}
}
