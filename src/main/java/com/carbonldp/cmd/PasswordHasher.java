package com.carbonldp.cmd;

import com.carbonldp.utils.AuthenticationUtil;

public class PasswordHasher {

	public static void main( String[] args ) {
		PasswordHasher hasher = new PasswordHasher();
		String password = "hello";
		hasher.hash( password );
	}

	private void hash( String password ) {
		String salt = AuthenticationUtil.generateRandomSalt();
		password = AuthenticationUtil.saltPassword( password, salt );
		password = AuthenticationUtil.hashPassword( password );

		System.out.println( "Salt: " + salt );
		System.out.println( "Hashed Password: " + password );
	}

}
