package com.carbonldp.authorization;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.carbonldp.authentication.TemporaryAuthorizationToken;

@Aspect
public class SecurityContextExchanger {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	// TODO: Add a kinded and a scoping designators to improve performance
	// See http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html#writing-good-pointcuts
	@Before("@annotation(runWith)")
	public void exchangeSecurityContext(RunWith runWith) {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> exchangeSecurityContext()");
		}

		String[] roles = runWith.roles();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}

		Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
		Authentication newAuthentication = new TemporaryAuthorizationToken(authorities, originalAuthentication);
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< exchangeSecurityContext() > Authentication exchanged in SecurityContext to: {}", newAuthentication);
		}
	}

	// TODO: Add a kinded and a scoping designators to improve performance
	// See http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html#writing-good-pointcuts
	@After("@annotation(runWith)")
	public void restoreSecurityContext(RunWith runWith) {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> restoreSecurityContext()");
		}

		Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! (currentAuthentication instanceof TemporaryAuthorizationToken) ) {
			// TODO: Throw exception. The authentication has changed during the method call
		}

		Authentication originalAuthentication = ((TemporaryAuthorizationToken) currentAuthentication).getOriginalAuthenticationObject();
		SecurityContextHolder.getContext().setAuthentication(originalAuthentication);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< restoreSecurityContext() > Authentication restored in SecurityContext to: {}", originalAuthentication);
		}
	}

}
