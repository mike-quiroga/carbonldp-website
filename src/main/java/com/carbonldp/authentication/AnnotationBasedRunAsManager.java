package com.carbonldp.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AnnotationBasedRunAsManager extends RunAsManagerImpl {

	@Override
	public Authentication buildRunAs(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		if ( ! (object instanceof ReflectiveMethodInvocation) ) {
			return super.buildRunAs(authentication, object, attributes);
		}

		// Check if the method invoked was annotated with RunWith
		ReflectiveMethodInvocation invocation = (ReflectiveMethodInvocation) object;
		RunWith annotation = invocation.getMethod().getAnnotation(RunWith.class);
		if ( annotation == null ) {
			// It wasn't
			return super.buildRunAs(authentication, object, attributes);
		}

		String[] roles = annotation.roles();

		if ( roles == null || roles.length == 0 ) return null;

		// Create and add all the role authorities
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			GrantedAuthority roleAuthority = new SimpleGrantedAuthority(role);
			authorities.add(roleAuthority);
		}

		return new RunAsUserToken(getKey(), authentication.getPrincipal(), authentication.getCredentials(), authorities, authentication.getClass());
	}
}
