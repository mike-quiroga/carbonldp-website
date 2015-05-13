package com.carbonldp.apps.roles.web;

import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( value = "apps/*/roles/" )
public class AppRolesController {
	@RequestMapping( method = RequestMethod.POST )
	public void createAppRole() {
		// TODO: Implement it
		throw new NotImplementedException();
	}
}
