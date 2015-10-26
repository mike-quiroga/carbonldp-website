package com.carbonldp.apps;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.app.AppAgentRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.RDFResourceUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class SesameAppService extends AbstractSesameLDPService implements AppService {
	private final AppRepository appRepository;
	private final AppRoleRepository appRoleRepository;
	private final AppAgentRepository appAgentsRepository;

	public SesameAppService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRepository appRepository, AppRoleRepository appRoleRepository, AppAgentRepository appAgentsRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		Assert.notNull( appRepository );
		this.appRepository = appRepository;
		Assert.notNull( appRoleRepository );
		this.appRoleRepository = appRoleRepository;
		this.appAgentsRepository = appAgentsRepository;
	}

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

		App createdApp = appRepository.create( app );
		ACL appACL = createAppACL( createdApp );

		AppRole adminRole = transactionWrapper.runInAppcontext( app, () -> {
			Container rootContainer = createRootContainer( app );
			ACL rootContainerACL = createRootContainerACL( rootContainer );

			Container appRolesContainer = appRoleRepository.createAppRolesContainer( rootContainer.getURI() );
			ACL appRolesContainerACL = createAppRolesContainerACL( appRolesContainer );

			AppRole appAdminRole = createAppAdminRole( appRolesContainer );
			ACL appAdminRoleACL = createAppAdminRoleACL( appAdminRole );

			Container appAgentsContainer = appAgentsRepository.createAppRolesContainer( rootContainer.getURI() );
			addCurrentAgentToAppAdminRole( appAdminRole );

			addAdminPermissions( appAdminRole, rootContainerACL );

			return appAdminRole;
		} );

		addAppDefaultPermissions( adminRole, appACL );

		sourceRepository.touch( createdApp.getURI() );
	}

	@Override
	public void delete( URI appURI ) {
		if ( ! exists( appURI ) ) throw new NotFoundException();
		appRepository.delete( appURI );
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

		sourceRepository.substract( appURI, resourceViewsToDelete );
		sourceRepository.add( appURI, resourceViewsToAdd );

		sourceRepository.touch( appURI );
	}

	private ACL createAppACL( App app ) {
		return aclRepository.createACL( app.getDocument() );
	}

	private BasicContainer createRootContainer( App app ) {
		BasicContainer rootContainer = BasicContainerFactory.getInstance().create( new RDFResource( app.getRootContainerURI() ) );
		containerRepository.create( rootContainer );
		return rootContainer;
	}

	private ACL createRootContainerACL( Container rootContainer ) {
		return aclRepository.createACL( rootContainer.getDocument() );
	}

	private ACL createAppRolesContainerACL( Container appRolesContainer ) {
		return aclRepository.createACL( appRolesContainer.getDocument() );
	}

	private AppRole createAppAdminRole( Container appRolesContainer ) {
		URI appAdminRoleURI = getAppAdminRoleURI( appRolesContainer );
		AppRole appAdminRole = AppRoleFactory.getInstance().create( new RDFResource( appAdminRoleURI ) );
		appAdminRole = appRoleRepository.create( appAdminRole );
		return appAdminRole;
	}

	private URI getAppAdminRoleURI( Container appRolesContainer ) {
		return URIUtil.createChildURI( appRolesContainer.getURI(), "app-admin/" );
	}

	private ACL createAppAdminRoleACL( AppRole appAdminRole ) {
		return aclRepository.createACL( appAdminRole.getDocument() );
	}

	private void addCurrentAgentToAppAdminRole( AppRole appAdminRole ) {
		Authentication rawAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if ( rawAuthentication == null ) throw new IllegalStateException( "The security context is empty" );
		if ( ! ( rawAuthentication instanceof AgentAuthenticationToken ) ) throw new IllegalStateException( "The authentication token isn't supported." );
		Agent agent = ( (AgentAuthenticationToken) rawAuthentication ).getAgent();
		appRoleRepository.addAgent( appAdminRole.getURI(), agent );
	}

	private void addAdminPermissions( AppRole appAdminRole, ACL rootContainerACL ) {
		aclRepository.grantPermissions( rootContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			// TODO: The app-admin role shouldn't have DELETE permissions on the rootContainer
			ACEDescription.Permission.DELETE,

			ACEDescription.Permission.CREATE_ACCESS_POINT,
			ACEDescription.Permission.CREATE_CHILD,
			ACEDescription.Permission.ADD_MEMBER
		), true );
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
}
