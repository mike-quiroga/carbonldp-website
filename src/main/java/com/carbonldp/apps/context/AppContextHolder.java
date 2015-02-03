package com.carbonldp.apps.context;

public final class AppContextHolder {
	private AppContextHolder() {
		// Meaning non-instantiable
	}

	private static AppContextHolderStrategy strategyHolder;
	// This could be extended to include more options
	private static Strategy strategy = Strategy.THREAD_LOCAL;

	static {
		populateStrategy();
	}

	private static void populateStrategy() {
		switch (strategy) {
			case THREAD_LOCAL:
				strategyHolder = new ThreadLocalAppContextHolderStrategy();
				break;
			default:
				break;
		}
	}

	public static AppContext getContext() {
		return strategyHolder.getContext();
	}

	public static void setContext(AppContext context) {
		strategyHolder.setContext(context);
	}

	public static void clearContext() {
		strategyHolder.clearContext();
	}

	public static AppContext createEmptyContext() {
		return strategyHolder.createEmptyContext();
	}

	public static enum Strategy {
		THREAD_LOCAL
	}
}
