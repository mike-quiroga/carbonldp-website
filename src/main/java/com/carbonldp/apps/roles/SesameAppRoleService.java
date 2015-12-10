package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.AlreadyHasAParentException;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

import java.util.List;
import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {
	private final ContainerService containerService;
	private final AppRoleRepository appRoleRepository;
	private final RDFSourceService sourceService;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, ContainerService containerService, AppRoleRepository appRoleRepository, RDFSourceService sourceService ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.appRoleRepository = appRoleRepository;
		this.containerService = containerService;
		this.sourceService = sourceService;
	}

	@Override
	public void create( AppRole appRole ) {
		if ( sourceRepository.exists( appRole.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

		containerService.createChild( appRoleRepository.getContainerURI(), appRole );
		createAgentsContainer( appRole );
	}

	private void createAgentsContainer( AppRole appRole ) {
		URI agentsContainerURI = appRoleRepository.getAgentsContainerURI( appRole.getURI() );
		RDFResource resource = new RDFResource( agentsContainerURI );
		DirectContainer container = DirectContainerFactory.getInstance().create( resource, appRole.getURI(), AppRoleDescription.Property.AGENT.getURI() );
		sourceService.createAccessPoint( appRole.getURI(), container );
	}

	@Override
	public void addChildren( URI parentRole, Set<URI> childs ) {
		for ( URI member : childs ) {
			addChild( parentRole, member );
		}
	}

	@Override
	public void addChild( URI parentRoleURI, URI child ) {
		if ( ( ! sourceRepository.exists( parentRoleURI ) ) || ( ! sourceRepository.exists( child ) ) ) throw new ResourceDoesntExistException();
		if ( ! sourceRepository.is( child, AppRoleDescription.Resource.CLASS ) ) throw new InvalidResourceException( new Infraction( 0x2001, "rdf.type", AppRoleDescription.Resource.CLASS.getURI().stringValue() ) );

		validateHasParent( child );
		containerService.addMember( parentRoleURI, child );

		DateTime modifiedTime = DateTime.now();
		sourceRepository.touch( parentRoleURI, modifiedTime );

	}

	private void validateHasParent( URI childURI ) {
		if ( ! sourceRepository.exists( childURI ) ) throw new ResourceDoesntExistException();
		Set<URI> parentsRoles = appRoleRepository.getParentsURI( childURI );
		if ( ! parentsRoles.isEmpty() ) throw new AlreadyHasAParentException();
	}

	private void validate( AppRole appRole ) {
		List<Infraction> infractions = AppRoleFactory.getInstance().validate( appRole );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}
}


