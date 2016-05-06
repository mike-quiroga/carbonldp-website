package com.carbonldp.authentication.LDAP.web;

import com.carbonldp.authentication.LDAP.app.LDAPServerService;
import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.authentication.LDAPServerFactory;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */

@RequestHandler
public class LDAPServersPostHandler extends AbstractRDFPostRequestHandler<LDAPServer> {
	protected LDAPServerService ldapServerService;

	@Override
	protected LDAPServer getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return LDAPServerFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( IRI targetIRI, LDAPServer documentResourceView ) {
		ldapServerService.create( targetIRI, documentResourceView );
	}

	@Autowired
	public void setLdapServerService( LDAPServerService ldapServerService ) { this.ldapServerService = ldapServerService; }
}
