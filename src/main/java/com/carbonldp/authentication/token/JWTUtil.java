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

	public static Map<String, Object> decode( String password ) throws UnsupportedJwtException, MalformedJwtException, SignatureException, ExpiredJwtException, IllegalArgumentException {
		return decode( password, null );
	}

	public static Map<String, Object> decode( String password, IRI targetIRI ) {
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
