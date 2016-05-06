package com.carbonldp.authentication.LDAP.app;

import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.IRI;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public interface AppLDAPServerRepository {
	public Container createAppLDAPServersContainer( IRI rootContainerIRI );
}
