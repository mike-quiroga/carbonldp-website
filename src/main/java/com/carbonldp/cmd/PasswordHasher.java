package com.carbonldp.cmd;

import com.carbonldp.utils.AuthenticationUtil;

import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

	public static void main(String[] args) {
		PasswordHasher hasher = new PasswordHasher();
		String password = "hello";
		hasher.hash( password );
	}

	private void hash(String password) {
		String salt = AuthenticationUtil.generateRandomSalt();
		password = AuthenticationUtil.saltPassword( password, salt );
		try {
			password = AuthenticationUtil.hashPassword( password );
		} catch ( NoSuchAlgorithmException e ) {
			throw new RuntimeException( e );
		}

		System.out.println( "Salt: " + salt );
		System.out.println( "Hashed Password: " + password );
	}

}
