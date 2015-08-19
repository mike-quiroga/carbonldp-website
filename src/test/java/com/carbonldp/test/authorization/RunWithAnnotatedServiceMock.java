package com.carbonldp.test.authorization;

import com.carbonldp.authentication.TemporaryAuthorizationToken;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.testng.Assert.*;

public class RunWithAnnotatedServiceMock {

	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	public void doSomethingWithAnotherSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		assertNotNull( authentication );
		assertTrue( authentication instanceof TemporaryAuthorizationToken );

		Set<Platform.Role> platformRoles = ( (TemporaryAuthorizationToken) authentication ).getPlatformRoles();
		assertTrue( platformRoles.contains( Platform.Role.SYSTEM ) );
		assertEquals( platformRoles.size(), 1 );
	}
}
