package com.carbonldp.apps;

import com.carbonldp.Vars;
import com.carbonldp.agents.Agent;
import com.carbonldp.agents.app.AppAgentRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.token.app.AppTokenRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.jobs.JobDescription;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.ServicesInvoker;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class SesameAppService extends AbstractSesameLDPService implements AppService {

	protected ContainerService containerService;

	protected AppRepository appRepository;
	protected AppRoleRepository appRoleRepository;
	protected AppAgentRepository appAgentRepository;
	protected AppTokenRepository appTokensRepository;
	protected AppRoleService appRoleService;
	protected RDFSourceService sourceService;
	protected ServicesInvoker servicesInvoker;

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
	public void create( App app ) {
		if ( exists( app.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( app );

		App createdApp = appRepository.createPlatformAppRepository( app );
		containerService.createChild( appRepository.getPlatformAppContainerURI(), app );
		ACL appACL = aclRepository.getResourceACL( createdApp.getURI() );
		if ( appACL == null ) {
			throw new IllegalStateException( "Resource couldn't be created" );
		}

		AppRole adminRole = transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
			Container rootContainer = createRootContainer( app );
			ACL rootContainerACL = createRootContainerACL( rootContainer );

			Container appRolesContainer = appRoleRepository.createAppRolesContainer( rootContainer.getURI() );
			ACL appRolesContainerACL = createAppRolesContainerACL( appRolesContainer );

			AppRole appAdminRole = createAppAdminRole( appRolesContainer );
			ACL appAdminRoleACL = createAppAdminRoleACL( appAdminRole );

			Container appAgentsContainer = appAgentRepository.createAppAgentsContainer( rootContainer.getURI() );
			ACL appAgentsContainerACL = createAppAgentsACL( appAgentsContainer );

			Container appTokensContainer = appTokensRepository.createAppTokensContainer( rootContainer.getURI() );
			ACL appTokensContainerACL = createAppTokensACL( appTokensContainer );

			addDefaultPermissions( appAdminRole, rootContainerACL );

			return appAdminRole;
		} );

		transactionWrapper.runInAppContext( app, () -> addCurrentAgentToAppAdminRole( adminRole ) );

		addAppDefaultPermissions( adminRole, appACL );

		servicesInvoker.proxy( ( proxy ) -> {
			( (SesameAppService) proxy.getAppService() ).createJobsContainer( app );
			( (SesameAppService) proxy.getAppService() ).createBackupContainer( app );
		} );

		sourceRepository.touch( createdApp.getURI() );
	}

	@Override
	public void delete( URI appURI ) {
		if ( ! exists( appURI ) ) throw new NotFoundException();
		appRepository.delete( appURI );
		sourceRepository.deleteOccurrences( appURI, true );
	}

	@Override
	public void replace( App app ) {
		URI appURI = app.getURI();
		validate( app );

		if ( ! exists( appURI ) ) throw new NotFoundException();
		sourceService.replace( app );
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

	private void validate( App app ) {
		List<Infraction> infractions = AppFactory.getInstance().validate( app );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@RunWith( platformRoles = Platform.Role.SYSTEM )
	public void createBackupContainer( App app ) {
		URI containerURI = generateBackupContainerURI( app );
		RDFResource backupsResource = new RDFResource( containerURI );
		DirectContainer backupsContainer = DirectContainerFactory.getInstance().create( backupsResource, app.getURI(), AppDescription.Property.BACKUP.getURI() );
		sourceService.createAccessPoint( app.getURI(), backupsContainer );
	}

	private URI generateBackupContainerURI( App app ) {
		String appString = app.getURI().stringValue();
		String backupsString = Vars.getInstance().getBackupsContainer();
		return new URIImpl( appString + backupsString );
	}

	@RunWith( platformRoles = Platform.Role.SYSTEM )
	public void createJobsContainer( App app ) {
		URI containerURI = generateJobsContainerURI( app );
		RDFResource jobsResource = new RDFResource( containerURI );
		DirectContainer jobsContainer = DirectContainerFactory.getInstance().create( jobsResource, app.getURI(), AppDescription.Property.JOB.getURI() );
		jobsContainer.setMemberOfRelation( JobDescription.Property.APP_RELATED.getURI() );
		sourceService.createAccessPoint( app.getURI(), jobsContainer );
	}

	private URI generateJobsContainerURI( App app ) {
		String appString = app.getURI().stringValue();
		String jobsString = Vars.getInstance().getJobsContainer();
		return new URIImpl( appString + jobsString );
	}

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
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

	@Autowired
	public void setServicesInvoker( ServicesInvoker servicesInvoker ) { this.servicesInvoker = servicesInvoker; }
}
