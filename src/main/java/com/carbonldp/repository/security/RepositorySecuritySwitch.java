package com.carbonldp.repository.security;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class RepositorySecuritySwitch {
	private static final ThreadLocal<Boolean> enabled = new ThreadLocal<>();

	public static boolean isEnabled() {
		Boolean enabled = RepositorySecuritySwitch.enabled.get();
		return ! ( enabled == null || ! enabled );
	}

	public static void enable() {
		RepositorySecuritySwitch.enabled.set( true );
	}

	public static void disable() {
		RepositorySecuritySwitch.enabled.remove();
	}
}
