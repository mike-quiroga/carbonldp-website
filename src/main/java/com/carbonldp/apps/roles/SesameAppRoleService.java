package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {
	private final AppRepository appRepository;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRepository appRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		Assert.notNull( appRepository );
		this.appRepository = appRepository;
	}

	@Override
	public boolean exists( URI appRoleURI ) {
		return appRepository.exists( appRoleURI );
	}

	@Override
	public void create( AppRole appRole ) {
		if ( exists( appRole.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

	}

	private void validate( AppRole appRole ) {
		List<Infraction> infractions = AppRoleFactory.getInstance().validate( appRole );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}
}
