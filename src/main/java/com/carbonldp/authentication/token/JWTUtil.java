package com.carbonldp.authentication.token;

import com.carbonldp.Vars;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public final class JWTUtil {

	private JWTUtil() {}

	private static JwtBuilder createJwtBuilder( String password ) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );
		Key signingKey = new SecretKeySpec( apiKeySecretBytes, signatureAlgorithm.getJcaName() );
		return Jwts.builder()
				   .setSubject( password )
				   .signWith( signatureAlgorithm, signingKey );
	}

	public static String encode( String password ) {
		JwtBuilder builder = createJwtBuilder( password );
		return builder.compact();
	}

	public static String encode( String password, Date expTime ) {
		JwtBuilder builder = createJwtBuilder( password );
		builder.setExpiration( expTime );
		return builder.compact();
	}

	public static String decode( String password ) throws UnsupportedJwtException, MalformedJwtException, SignatureException, ExpiredJwtException, IllegalArgumentException {
		byte[] signingKey;

		signingKey = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );

		return Jwts.parser()
				   .setSigningKey( signingKey )
				   .parseClaimsJws( password )
				   .getBody()
				   .getSubject();
	}

}
