package com.carbonldp.authentication.LDAP.app;

import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.authentication.LDAPServerFactory;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.models.Infraction;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public class SesameLDAPServerService extends AbstractSesameLDPService implements LDAPServerService {

	private ContainerService containerService;

	public void create( IRI targetIRI, LDAPServer ldapServer ) {
		validate( ldapServer );
		containerService.createChild( targetIRI, ldapServer );
	}

	private void validate( LDAPServer ldapServer ) {
		List<Infraction> infractions = LDAPServerFactory.getInstance().validate( ldapServer );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }
}
