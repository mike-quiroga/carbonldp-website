package com.carbonldp.test.authentication;

import com.carbonldp.test.AbstractIT;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class UsernameAndPasswordAuthenticationIT extends AbstractIT {

	@Test
	public void rightUsernameAndPassword() {
		Authentication authentication = Mockito.mock( Authentication.class );

		Mockito.when( authentication.getPrincipal() ).thenReturn( "admin@carbonldp.com" );
		Mockito.when( authentication.getCredentials() ).thenReturn( "hello" );

		try {
			sesameUsernamePasswordAuthenticationProvider.authenticate( authentication );
		} catch ( BadCredentialsException e ) {
			fail();
		}
	}

	@Test
	public void invalidPassword() {
		Authentication authentication = Mockito.mock( Authentication.class );

		Mockito.when( authentication.getPrincipal() ).thenReturn( "admin@carbonldp.com" );
		Mockito.when( authentication.getCredentials() ).thenReturn( "person" );

		try {
			sesameUsernamePasswordAuthenticationProvider.authenticate( authentication );
			fail();
		} catch ( BadCredentialsException e ) {

		}

	}

	@Test
	public void invalidUsername() {
		Authentication authentication = Mockito.mock( Authentication.class );

		Mockito.when( authentication.getPrincipal() ).thenReturn( "invalid@carbonldp.com" );
		Mockito.when( authentication.getCredentials() ).thenReturn( "username" );
		//TODO LDP-376
		try {
			sesameUsernamePasswordAuthenticationProvider.authenticate( authentication );
			fail();
		} catch ( BadCredentialsException e ) {

		}

	}

	@Test
	public void credentialsNotStrings() {
		Authentication authentication = Mockito.mock( Authentication.class );

		Mockito.when( authentication.getPrincipal() ).thenReturn( 1 );
		Mockito.when( authentication.getCredentials() ).thenReturn( 2 );

		try {
			sesameUsernamePasswordAuthenticationProvider.authenticate( authentication );
			fail();
		} catch ( BadCredentialsException e ) {

		}

	}
}
