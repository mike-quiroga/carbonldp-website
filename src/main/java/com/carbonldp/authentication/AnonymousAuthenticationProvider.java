package com.carbonldp.authentication;

import com.carbonldp.AbstractComponent;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authorization.*;
import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AnonymousAuthenticationProvider extends AbstractComponent {
	private final PlatformRoleRepository platformRoleRepository;
	private final PlatformPrivilegeRepository platformPrivilegeRepository;

	private final Set<Platform.Role> defaultPlatformRoleRepresentations;

	public AnonymousAuthenticationProvider( PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		this.platformPrivilegeRepository = platformPrivilegeRepository;
		this.platformRoleRepository = platformRoleRepository;

		Set<Platform.Role> defaultPlatformRoleRepresentations = new HashSet<>();
		defaultPlatformRoleRepresentations.add( Platform.Role.ANONYMOUS );
		this.defaultPlatformRoleRepresentations = Collections.unmodifiableSet( defaultPlatformRoleRepresentations );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInPlatformContext
	public Authentication authenticate() {
		Set<PlatformRole> platformRoles = getPlatformRoles();
		Set<PlatformPrivilege> platformPrivileges = platformPrivilegeRepository.get( platformRoles );

		Set<Platform.Privilege> platformPrivilegeRepresentations = platformPrivilegeRepository.getRepresentations( platformPrivileges );

		return new AnonymousAuthenticationToken( this.defaultPlatformRoleRepresentations, platformPrivilegeRepresentations );
	}

	private Set<PlatformRole> getPlatformRoles() {
		Set<URI> platformRoleURIs = new HashSet<>();
		for ( Platform.Role platformRoleDescription : this.defaultPlatformRoleRepresentations ) {
			platformRoleURIs.addAll( Arrays.asList( platformRoleDescription.getIRIs() ) );
		}
		return platformRoleRepository.get( platformRoleURIs );
	}
}
