package com.carbonldp.test;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.apps.context.RunInPlatformContext;

public class ApplicationContextActionTemplate {

	@RunInAppContext
	public void runInAppContext(App application, ActionCallback action) {
		action.run();
	}

	@RunInPlatformContext
	public void runInPlatformContext(ActionCallback action) {
		action.run();
	}
}
