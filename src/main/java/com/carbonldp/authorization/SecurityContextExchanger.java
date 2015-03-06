package com.carbonldp.authorization;

import com.carbonldp.AbstractAspect;
import com.carbonldp.authentication.TemporaryAuthorizationToken;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

@Aspect
public class SecurityContextExchanger extends AbstractAspect {

	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );

	@Before( "inCarbonLDPPackage() && @annotation(runWith)" )
	public void exchangeSecurityContext(RunWith runWith) {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace( ">> exchangeSecurityContext()" );
		}

		List<Platform.Role> platformRoles = Arrays.asList( runWith.platformRoles() );
		List<Platform.Privilege> platformPrivileges = Arrays.asList( runWith.platformPrivileges() );

		Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
		Authentication newAuthentication = new TemporaryAuthorizationToken( originalAuthentication, platformRoles, platformPrivileges );

		SecurityContextHolder.getContext().setAuthentication( newAuthentication );

		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "<< exchangeSecurityContext() > Authentication exchanged in SecurityContext to: {}", newAuthentication );
		}
	}

	@After( "inCarbonLDPPackage() && @annotation(runWith)" )
	public void restoreSecurityContext(RunWith runWith) {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace( ">> restoreSecurityContext()" );
		}

		Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if ( !(currentAuthentication instanceof TemporaryAuthorizationToken) ) {
			// TODO: Throw exception. The authentication has changed during the method call
		}

		Authentication originalAuthentication = ((TemporaryAuthorizationToken) currentAuthentication).getOriginalAuthenticationObject();
		SecurityContextHolder.getContext().setAuthentication( originalAuthentication );

		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "<< restoreSecurityContext() > Authentication restored in SecurityContext to: {}", originalAuthentication );
		}
	}

}