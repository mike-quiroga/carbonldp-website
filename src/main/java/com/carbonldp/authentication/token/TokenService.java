package com.carbonldp.authentication.token;

import com.carbonldp.authentication.Token;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since 0.15.0-ALPHA
 */
public interface TokenService {
	@PreAuthorize( "isAuthenticated() and !( hasRole('ROLE_ANONYMOUS') )" )
	public Token createToken();
}
