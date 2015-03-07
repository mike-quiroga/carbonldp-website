package com.carbonldp.test;

import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.RunInAppContext;

public class ApplicationContextActionTemplate {
	@Transactional
	@RunInAppContext
	public void runInPlatformContext(App application, ActionCallback action) {
		action.run();
	}
}
