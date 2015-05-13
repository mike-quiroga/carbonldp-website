package com.carbonldp.web.cors;

public class CORSPlatformContextFilter extends CORSContextFilter {
	public static final String FILTER_APPLIED = "__carbon_cpcf_applied";

	public CORSPlatformContextFilter() {
		super( FILTER_APPLIED );
	}

}
