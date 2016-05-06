package com.carbonldp.authentication.LDAP.app;

import com.carbonldp.authentication.LDAPServer;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public interface LDAPServerService {

	@PreAuthorize( "hasPermission(#targetIRI, 'CREATE_CHILD')" )
	public void create(IRI targetIRI, LDAPServer ldapServer );
}
