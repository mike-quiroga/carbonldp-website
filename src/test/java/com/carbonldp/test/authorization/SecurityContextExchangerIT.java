package com.carbonldp.test.authorization;

import static org.testng.Assert.assertNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.annotations.Test;

import com.carbonldp.test.AbstractIT;

public class SecurityContextExchangerIT extends AbstractIT {

	@Autowired
	private RunWithAnnotatedServiceMock serviceMock;

	@Test
	public void securityContextChange() {
		if ( SecurityContextHolder.getContext().getAuthentication() != null ) {
			SecurityContextHolder.getContext().setAuthentication(null);
		}

		serviceMock.doSomethingWithAnotherSecurityContext();

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
}
