package com.carbonldp.authentication.LDAP.app;

import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.authentication.LDAPServerDescription;
import com.carbonldp.authentication.LDAPServerFactory;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.models.Infraction;
import com.carbonldp.utils.LDAPUtil;
import com.carbonldp.utils.SPARQLUtil;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.List;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public class SesameLDAPServerService extends AbstractSesameLDPService implements LDAPServerService {

	private ContainerService containerService;

	@Override
	public void create( IRI targetIRI, LDAPServer ldapServer ) {
		validate( ldapServer );
		containerService.createChild( targetIRI, ldapServer );
	}

	@Override
	public LDAPServer get( IRI targetIRI ) {
		return new LDAPServer( sourceRepository.get( targetIRI ) );
	}

	@Override
	public void registerLDAPAgents( LDAPServer ldapServer, Set<String> usernameFields ) {
		LdapTemplate ldapTemplate = LDAPUtil.getLDAPTemplate( ldapServer );


	}

	private void validate( LDAPServer ldapServer ) {
		List<Infraction> infractions = LDAPServerFactory.getInstance().validate( ldapServer );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }
}
