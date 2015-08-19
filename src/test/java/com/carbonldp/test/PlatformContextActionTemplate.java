package com.carbonldp.test;

import com.carbonldp.apps.context.RunInPlatformContext;

public class PlatformContextActionTemplate {

	@RunInPlatformContext
	public void runInPlatformContext( ActionCallback action ) {
		action.run();
	}
}