package com.carbonldp.authentication.token;

import com.carbonldp.Vars;
import com.carbonldp.exceptions.StupidityException;
import io.jsonwebtoken.*;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.authentication.BadCredentialsException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public final class JWTUtil {

	private JWTUtil() {}

	private static JwtBuilder createJwtBuilder( String subject ) {return createJwtBuilder( subject, null, null );}

	private static JwtBuilder createJwtBuilder( String subject, Map<String, Object> claims, Date expTime ) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );
		Key signingKey = new SecretKeySpec( apiKeySecretBytes, signatureAlgorithm.getJcaName() );

		JwtBuilder builder = Jwts.builder();
		if ( claims != null ) builder = builder.setClaims( claims );
		builder = builder.setSubject( subject );
		builder = builder.signWith( signatureAlgorithm, signingKey );
		if ( expTime != null ) builder = builder.setExpiration( expTime );
		return builder;
	}

	public static String encodeLDAPPassword( String password ) {
		JwtBuilder builder = createJwtBuilder( password );
		return builder.compact();
	}

	public static String encodeToken( String agentToken, Date expTime ) {
		return encodeToken( null, agentToken, expTime );
	}

	public static String encodeToken( IRI appIRI, String agentToken, Date expTime ) {
		Map<String, Object> claims = new HashMap<>();
		if ( appIRI != null ) claims.put( "appRelated", appIRI.stringValue() );
		JwtBuilder builder = createJwtBuilder( agentToken, claims, expTime );
		return builder.compact();
	}

	public static String encodeTicket( String agentToken, Date expTime, IRI targetIRI ) {return encodeTicket( null, agentToken, expTime, targetIRI );}

	public static String encodeTicket( IRI appIRI, String agentToken, Date expTime, IRI targetIRI ) {

		Map<String, Object> claims = new HashMap<>();
		claims.put( "targetIRI", targetIRI );
		if ( appIRI != null ) claims.put( "appRelated", appIRI.stringValue() );
		JwtBuilder builder = createJwtBuilder( agentToken, claims, expTime );
		return builder.compact();

	}

	public static Map<String, Object> decode( String password ) {
		byte[] signingKey;
		try {
			signingKey = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );
		} catch ( IllegalArgumentException e ) {
			throw new StupidityException( e );
		}
		try {
			Claims claims = Jwts
				.parser()
				.setSigningKey( signingKey )
				.parseClaimsJws( password )
				.getBody();
			return claims;
		} catch ( UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException | IllegalArgumentException e ) {
			throw new BadCredentialsException( "The JSON Web Token isn't valid, nested exception: ", e );
		}
	}
}
