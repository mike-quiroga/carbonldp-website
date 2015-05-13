package com.carbonldp.authentication;

import com.carbonldp.authorization.Platform;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.*;

import static com.carbonldp.Consts.*;

public abstract class AbstractAuthenticationToken implements Authentication {
	private static final long serialVersionUID = - 636371825136099459L;

	protected final Set<GrantedAuthority> grantedAuthorities;

	protected final Set<Platform.Role> platformRoles;
	protected final Set<Platform.Privilege> platformPrivileges;

	private boolean authenticated = false;

	public AbstractAuthenticationToken( Collection<Platform.Role> platformRoles, Collection<Platform.Privilege> platformPrivileges ) {
		Set<GrantedAuthority> tempAuthorities = new HashSet<GrantedAuthority>();

		this.platformRoles = getUnmodifiableCopy( platformRoles );
		addPlatformRoles( tempAuthorities );

		this.platformPrivileges = getUnmodifiableCopy( platformPrivileges );
		addPlatformPrivileges( tempAuthorities );

		this.grantedAuthorities = getUnmodifiableCopy( tempAuthorities );

	}

	@Override
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	public Set<Platform.Role> getPlatformRoles() {
		return this.platformRoles;
	}

	public Set<Platform.Privilege> getPlatformPrivileges() {
		return this.platformPrivileges;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.grantedAuthorities;
	}

	@Override
	public void setAuthenticated( boolean authenticated ) throws IllegalArgumentException {
		this.authenticated = authenticated;
	}

	private <E> Set<E> getUnmodifiableCopy( Collection<E> items ) {
		Set<E> temp = new HashSet<E>();
		for ( E item : items ) {
			Assert.notNull( item );
			temp.add( item );
		}
		return Collections.unmodifiableSet( temp );
	}

	private void addPlatformRoles( Set<GrantedAuthority> tempAuthorities ) {
		for ( Platform.Role platformRole : this.platformRoles ) {
			String authorityName = ( new StringBuilder() ).append( PLATFORM_ROLE_PREFIX )
														  .append( platformRole.name() )
														  .toString();
			tempAuthorities.add( new SimpleGrantedAuthority( authorityName ) );
		}
	}

	private void addPlatformPrivileges( Set<GrantedAuthority> tempAuthorities ) {
		for ( Platform.Privilege platformPrivilege : this.platformPrivileges ) {
			String authorityName = ( new StringBuilder() ).append( PLATFORM_PRIVILEGE_PREFIX )
														  .append( platformPrivilege.name() )
														  .toString();
			tempAuthorities.add( new SimpleGrantedAuthority( authorityName ) );
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
			.append( super.toString() ).append( ": (" )
			.append( this.getName() ).append( COMMA ).append( SPACE )
			.append( "Authenticated: " ).append( this.isAuthenticated() ).append( COMMA ).append( SPACE )
			.append( "PlatformRoles: (" )
		;

		Iterator<Platform.Role> platformRoleIterator = this.platformRoles.iterator();
		if ( ! platformRoleIterator.hasNext() ) stringBuilder.append( "- NONE -" );
		while ( platformRoleIterator.hasNext() ) {
			stringBuilder.append( platformRoleIterator.next().name() );
			if ( platformRoleIterator.hasNext() ) stringBuilder.append( COMMA ).append( SPACE );
		}

		stringBuilder
			.append( ")" ).append( COMMA ).append( SPACE )
			.append( "PlatformPrivileges: (" )
		;

		Iterator<Platform.Privilege> platformPrivilegeIterator = this.platformPrivileges.iterator();
		if ( ! platformPrivilegeIterator.hasNext() ) stringBuilder.append( "- NONE -" );
		while ( platformPrivilegeIterator.hasNext() ) {
			stringBuilder.append( platformPrivilegeIterator.next().name() ).append( COMMA ).append( SPACE );
			if ( platformRoleIterator.hasNext() ) stringBuilder.append( COMMA ).append( SPACE );
		}

		stringBuilder
			.append( "))" )
		;

		return stringBuilder.toString();
	}

}
