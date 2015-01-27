package com.carbonldp.test.authorization;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.carbonldp.authentication.TemporaryAuthorizationToken;
import com.carbonldp.authorization.RunWith;

public class RunWithAnnotatedServiceMock {

	@RunWith(roles = { "ROLE_A", "ROLE_B" })
	public void doSomethingWithAnotherSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		assertNotNull(authentication);
		assertTrue(authentication instanceof TemporaryAuthorizationToken);

		Collection<GrantedAuthority> authorities = ((TemporaryAuthorizationToken) authentication).getAuthorities();
		GrantedAuthority roleA = new SimpleGrantedAuthority("ROLE_A");
		GrantedAuthority roleB = new SimpleGrantedAuthority("ROLE_B");
		assertTrue(authorities.contains(roleA));
		assertTrue(authorities.contains(roleB));
	}
}
