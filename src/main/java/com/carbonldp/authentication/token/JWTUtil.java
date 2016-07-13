package com.carbonldp.authentication.token;

import com.carbonldp.Vars;
import io.jsonwebtoken.*;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.authentication.BadCredentialsException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
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
		return decode( password, null );
	}

	public static String decode( String password, IRI targetIRI ) throws UnsupportedJwtException, MalformedJwtException, SignatureException, ExpiredJwtException, IllegalArgumentException {
		byte[] signingKey;

		signingKey = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );

		Claims claims = Jwts
			.parser()
			.setSigningKey( signingKey )
			.parseClaimsJws( password )
			.getBody();
		validateTargetIRI( claims, targetIRI );
		return claims.getSubject();
	}

	private static void validateTargetIRI( Claims claims, IRI targetIRI ) {
		Map targetIRIClaims = (Map) claims.get( "targetIRI" );
		if ( targetIRIClaims != null && targetIRI == null ) throw new BadCredentialsException( "invalid target IRI" );

		if ( targetIRI != null ) {
			if ( targetIRIClaims == null ) throw new BadCredentialsException( "invalid target IRI" );
			String tokenTargetIRI = (String) targetIRIClaims.get( "namespace" );
			if ( ! tokenTargetIRI.equals( targetIRI.stringValue() ) ) throw new BadCredentialsException( "invalid target IRI" );
		}
	}
}
