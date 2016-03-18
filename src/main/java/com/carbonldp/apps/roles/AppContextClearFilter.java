package com.carbonldp.apps.roles;

import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.web.AbstractUniqueFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since 0.28.4-ALPHA
 */
public class AppContextClearFilter extends AbstractUniqueFilter {

	public static final String FILTER_APPLIED = "__carbon_accf_applied";

	public AppContextClearFilter() {
		super( FILTER_APPLIED );
	}

	@Override
	protected void applyFilter( HttpServletRequest request, HttpServletResponse response ) {
		AppContextHolder.clearContext();
	}
}
