package com.carbonldp.apps;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.app.AppAgentRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.token.app.AppTokenRepository;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFResourceUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SesameAppService extends AbstractSesameLDPService implements AppService {

	protected ContainerService containerService;

	protected AppRepository appRepository;
	protected AppRoleRepository appRoleRepository;
	protected AppAgentRepository appAgentRepository;
	protected AppTokenRepository appTokensRepository;
	protected AppRoleService appRoleService;

	@Override
	public boolean exists( URI appURI ) {
		return appRepository.exists( appURI );
	}

	@Override
	public App get( URI appURI ) {
		if ( ! exists( appURI ) ) throw new ResourceDoesntExistException();
		return appRepository.get( appURI );
	}

	@Override
	public Set<String> getDomains( URI appURI ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	@Override
	public void create( App app ) {
		if ( exists( app.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( app );

		App createdApp = appRepository.createPlatformAppRepository( app );
		containerService.createChild( appRepository.getPlatformAppContainerURI(), app );
		ACL appACL = createAppACL( createdApp );

		AppRole adminRole = transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
			Container rootContainer = createRootContainer( app );
			ACL rootContainerACL = createRootContainerACL( rootContainer );

			Container appRolesContainer = appRoleRepository.createAppRolesContainer( rootContainer.getURI() );
			ACL appRolesContainerACL = createAppRolesContainerACL( appRolesContainer );
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			AppRole appAdminRole = createAppAdminRole( appRolesContainer );
			ACL appAdminRoleACL = createAppAdminRoleACL( appAdminRole );

			Container appAgentsContainer = appAgentRepository.createAppAgentsContainer( rootContainer.getURI() );
			ACL appAgentsContainerACL = createAppAgentsACL( appAgentsContainer );

			Container appTokensContainer = appTokensRepository.createAppTokensContainer( rootContainer.getURI() );
			ACL appTokensContainerACL = createAppTokensACL( appTokensContainer );

			addDefaultPermissions( appAdminRole, rootContainerACL );

			return appAdminRole;
		} );

		transactionWrapper.runInAppcontext( app, () -> addCurrentAgentToAppAdminRole( adminRole ) );

		addAppDefaultPermissions( adminRole, appACL );

		sourceRepository.touch( createdApp.getURI() );
	}

	@Override
	public void delete( URI appURI ) {
		if ( ! exists( appURI ) ) throw new NotFoundException();
		appRepository.delete( appURI );
		sourceRepository.deleteOccurrences( appURI, true );
	}

	@Override
	public void addDomain( String domain ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	@Override
	public void setDomains( Set<String> domains ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	@Override
	public void removeDomain( String domain ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	@Override
	public void replace( App app ) {
		URI appURI = app.getURI();
		validate( app );

		if ( ! exists( appURI ) ) throw new NotFoundException();
		App originalApp = appRepository.get( appURI );
		RDFDocument originalDocument = originalApp.getDocument();

		RDFDocument newDocument = app.getDocument();

		Set<Statement> statementsToAdd = newDocument.stream().filter( statement -> ! originalDocument.contains( statement ) ).collect( Collectors.toSet() );
		Set<RDFResource> resourceViewsToAdd = RDFResourceUtil.getResourceViews( statementsToAdd );
		validateSystemProperties( resourceViewsToAdd );

		Set<Statement> statementsToDelete = originalDocument.stream().filter( statement -> ! newDocument.contains( statement ) ).collect( Collectors.toSet() );
		Set<RDFResource> resourceViewsToDelete = RDFResourceUtil.getResourceViews( statementsToDelete );
		validateSystemProperties( resourceViewsToDelete );

		sourceRepository.subtract( appURI, resourceViewsToDelete );
		sourceRepository.add( appURI, resourceViewsToAdd );

		sourceRepository.touch( appURI );
	}

	private ACL createAppACL( App app ) {
		return aclRepository.createACL( app.getURI() );
	}

	private BasicContainer createRootContainer( App app ) {
		BasicContainer rootContainer = BasicContainerFactory.getInstance().create( new RDFResource( app.getRootContainerURI() ) );
		containerRepository.create( rootContainer );
		return rootContainer;
	}

	private ACL createRootContainerACL( Container rootContainer ) {
		return aclRepository.createACL( rootContainer.getURI() );
	}

	private ACL createAppRolesContainerACL( Container appRolesContainer ) {
		return aclRepository.createACL( appRolesContainer.getURI() );
	}

	private ACL createAppAgentsACL( Container appAgentsContainer ) {
		return aclRepository.createACL( appAgentsContainer.getURI() );
	}

	private ACL createAppTokensACL( Container appTokensContainer ) {
		return aclRepository.createACL( appTokensContainer.getURI() );
	}

	private AppRole createAppAdminRole( Container appRolesContainer ) {
		URI appAdminRoleURI = getAppAdminRoleURI( appRolesContainer );
		AppRole appAdminRole = AppRoleFactory.getInstance().create( new RDFResource( appAdminRoleURI ) );
		appAdminRole.setName( "App admin." );
		appRoleService.create( appAdminRole );
		return appAdminRole;
	}

	private URI getAppAdminRoleURI( Container appRolesContainer ) {
		return URIUtil.createChildURI( appRolesContainer.getURI(), "app-admin/" );
	}

	private ACL createAppAdminRoleACL( AppRole appAdminRole ) {
		return aclRepository.createACL( appAdminRole.getURI() );
	}

	private void addCurrentAgentToAppAdminRole( AppRole appAdminRole ) {
		Authentication rawAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if ( rawAuthentication == null ) throw new IllegalStateException( "The security context is empty" );
		if ( ! ( rawAuthentication instanceof AgentAuthenticationToken ) ) throw new IllegalStateException( "The authentication token isn't supported." );
		Agent agent = ( (AgentAuthenticationToken) rawAuthentication ).getAgent();
		appRoleRepository.addAgent( appAdminRole.getURI(), agent );
	}

	private void addDefaultPermissions( AppRole appAdminRole, ACL rootContainerACL ) {
		aclRepository.grantPermissions( rootContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.CREATE_ACCESS_POINT,
			ACEDescription.Permission.CREATE_CHILD,
			ACEDescription.Permission.UPLOAD,
			ACEDescription.Permission.DOWNLOAD,
			ACEDescription.Permission.EXTEND,
			ACEDescription.Permission.ADD_MEMBER,
			ACEDescription.Permission.REMOVE_MEMBER
		), false );
		aclRepository.addInheritablePermissions( rootContainerACL, Arrays.asList( appAdminRole ), Arrays.asList( ACEDescription.Permission.values() ), true );
	}

	private void addAppDefaultPermissions( AppRole adminRole, ACL appACL ) {
		aclRepository.grantPermissions( appACL, Arrays.asList( adminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), true );
	}

	private void validateSystemProperties( Set<RDFResource> resourceViews ) {
		List<Infraction> infractions = new ArrayList<>();
		for ( RDFResource resource : resourceViews ) {
			infractions.addAll( AppFactory.getInstance().validateSystemManagedProperties( resource ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void validate( App app ) {
		List<Infraction> infractions = AppFactory.getInstance().validate( app );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setAppRepository( AppRepository appRepository ) { this.appRepository = appRepository; }

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) { this.appRoleRepository = appRoleRepository; }

	@Autowired
	public void setAppAgentRepository( AppAgentRepository appAgentRepository ) { this.appAgentRepository = appAgentRepository; }

	@Autowired
	public void setAppTokensRepository( AppTokenRepository appTokensRepository ) { this.appTokensRepository = appTokensRepository; }

	@Autowired
	public void setAppRoleService( AppRoleService appRoleService ) { this.appRoleService = appRoleService; }
}
