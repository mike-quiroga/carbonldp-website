package com.carbonldp.authentication.web;

import com.carbonldp.Vars;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.TokenRepresentation;
import com.carbonldp.authentication.TokenRepresentationDescription;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.rdf.RDFResourceDescription;
import com.carbonldp.web.RequestHandler;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

/**
 * @author NestorVenegas
 * @since _version_
 */
@RequestHandler
public class AuthenticationRequestHandler extends AbstractLDPRequestHandler {

	@Autowired
	TokenService tokenService;

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new RuntimeException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );
		Key signingKey = new SecretKeySpec( apiKeySecretBytes, signatureAlgorithm.getJcaName() );

		JwtBuilder builder = Jwts.builder()
								 .setSubject( agentToken.getAgent().getSubject().stringValue() )
								 .signWith( signatureAlgorithm, signingKey );

		TokenRepresentation tokenRepresentation = getTokenRepresentation( builder.compact() );
		return new ResponseEntity<>( tokenRepresentation, HttpStatus.OK );

	}

	private TokenRepresentation getTokenRepresentation( String token ) {
		TokenRepresentation tokenRepresentation = new TokenRepresentation();
		tokenRepresentation.addType( TokenRepresentationDescription.Resource.CLASS.getURI() );
		tokenRepresentation.addType( RDFResourceDescription.Resource.VOLATILE.getURI() );
		tokenRepresentation.setTokenKey( token );
		return tokenRepresentation;
	}

}
