package com.carbonldp.apps.context;

public final class ApplicationContextHolder {
	private ApplicationContextHolder() {
		// Meaning non-instantiable
	}

	private static ApplicationContextHolderStrategy strategyHolder;
	// This could be extended to include more options
	private static Strategy strategy = Strategy.THREAD_LOCAL;

	static {
		populateStrategy();
	}

	private static void populateStrategy() {
		switch (strategy) {
			case THREAD_LOCAL:
				strategyHolder = new ThreadLocalApplicationContextHolderStrategy();
				break;
			default:
				break;
		}
	}

	public static ApplicationContext getContext() {
		return strategyHolder.getContext();
	}

	public static void setContext(ApplicationContext context) {
		strategyHolder.setContext(context);
	}

	public static void clearContext() {
		strategyHolder.clearContext();
	}

	public static ApplicationContext createEmptyContext() {
		return strategyHolder.createEmptyContext();
	}

	public static enum Strategy {
		THREAD_LOCAL
	}
}
