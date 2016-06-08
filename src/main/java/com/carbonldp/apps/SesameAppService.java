package com.carbonldp.apps;

import com.carbonldp.Vars;
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
import com.carbonldp.jobs.JobDescription;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.namespaces.LDP;
import com.carbonldp.rdf.RDFListFactory;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class SesameAppService extends AbstractSesameLDPService implements AppService {

	private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

	protected ContainerService containerService;
	protected AppRepository appRepository;
	protected AppRoleRepository appRoleRepository;
	protected AppAgentRepository appAgentRepository;
	protected AppTokenRepository appTokensRepository;
	protected AppRoleService appRoleService;
	protected RDFSourceService sourceService;

	@Override
	public boolean exists( IRI appIRI ) {
		return appRepository.exists( appIRI );
	}

	@Override
	public App get( IRI appIRI ) {
		if ( ! exists( appIRI ) ) throw new ResourceDoesntExistException();
		return appRepository.get( appIRI );
	}

	@Override
	public void create( App app ) {
		if ( exists( app.getIRI() ) ) throw new ResourceAlreadyExistsException();
		validate( app );

		App createdApp = appRepository.createPlatformAppRepository( app );
		containerService.createChild( appRepository.getPlatformAppContainerIRI(), app );
		ACL appACL = aclRepository.getResourceACL( createdApp.getIRI() );
		if ( appACL == null ) {
			throw new IllegalStateException( "Resource couldn't be created" );
		}
		createBackupContainer( app );
		createJobsContainer( app );
		createLDAPServersContainer( app );

		AppRole adminRole = transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
			Container rootContainer = createRootContainer( app );
			ACL rootContainerACL = aclRepository.createACL( rootContainer.getIRI() );

			Container appRolesContainer = appRoleRepository.createAppRolesContainer( rootContainer.getIRI() );
			aclRepository.createACL( appRolesContainer.getIRI() );

			AppRole appAdminRole = createAppAdminRole( appRolesContainer );
			aclRepository.createACL( appAdminRole.getIRI() );

			Container appAgentsContainer = appAgentRepository.createAppAgentsContainer( rootContainer.getIRI() );
			aclRepository.createACL( appAgentsContainer.getIRI() );

			Container appTokensContainer = appTokensRepository.createAppTokensContainer( rootContainer.getIRI() );
			aclRepository.createACL( appTokensContainer.getIRI() );

			Container appTicketsContainer = appTokensRepository.createTicketsContainer( rootContainer.getIRI() );
			aclRepository.createACL( appTicketsContainer.getIRI() );

			addDefaultPermissions( appAdminRole, rootContainerACL );

			return appAdminRole;
		} );

		transactionWrapper.runInAppContext( app, () -> addCurrentAgentToAppAdminRole( adminRole ) );

		addAppDefaultPermissions( adminRole, appACL );

		sourceRepository.touch( createdApp.getIRI() );
	}

	@Override
	public void delete( IRI appIRI ) {
		if ( ! exists( appIRI ) ) throw new NotFoundException();
		appRepository.delete( appIRI );
	}

	@Override
	public void replace( App app ) {
		IRI appIRI = app.getIRI();
		validate( app );

		if ( ! exists( appIRI ) ) throw new NotFoundException();
		sourceService.replace( app );
	}

	private BasicContainer createRootContainer( App app ) {
		BasicContainer rootContainer = BasicContainerFactory.getInstance().create( new RDFResource( app.getRootContainerIRI() ) );
		containerRepository.create( rootContainer );
		return rootContainer;
	}

	private AppRole createAppAdminRole( Container appRolesContainer ) {
		IRI appAdminRoleIRI = getAppAdminRoleIRI( appRolesContainer );
		AppRole appAdminRole = AppRoleFactory.getInstance().create( new RDFResource( appAdminRoleIRI ) );
		appAdminRole.setName( "App admin." );
		appRoleService.create( appAdminRole );
		return appAdminRole;
	}

	private IRI getAppAdminRoleIRI( Container appRolesContainer ) {
		return IRIUtil.createChildIRI( appRolesContainer.getIRI(), "app-admin/" );
	}

	private void addCurrentAgentToAppAdminRole( AppRole appAdminRole ) {
		Authentication rawAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if ( rawAuthentication == null ) throw new IllegalStateException( "The security context is empty" );
		if ( ! ( rawAuthentication instanceof AgentAuthenticationToken ) ) throw new IllegalStateException( "The authentication token isn't supported." );
		Agent agent = ( (AgentAuthenticationToken) rawAuthentication ).getAgent();
		appRoleRepository.addAgent( appAdminRole.getIRI(), agent );
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

	private void createBackupContainer( App app ) {
		IRI containerIRI = generateBackupContainerIRI( app );
		RDFResource backupsResource = new RDFResource( containerIRI );
		BasicContainer backupsContainer = BasicContainerFactory.getInstance().create( backupsResource );
		containerRepository.createChild( app.getIRI(), backupsContainer );
		aclRepository.createACL( backupsContainer.getIRI() );
	}

	private IRI generateBackupContainerIRI( App app ) {
		String appString = app.getIRI().stringValue();
		String backupsString = Vars.getInstance().getBackupsContainer();
		return valueFactory.createIRI( appString + backupsString );
	}

	private void createJobsContainer( App app ) {
		IRI containerIRI = generateJobsContainerIRI( app );
		RDFResource jobsResource = new RDFResource( containerIRI );
		BasicContainer jobsContainer = BasicContainerFactory.getInstance().create( jobsResource, valueFactory.createIRI( LDP.Properties.MEMBER ), JobDescription.Property.EXECUTION_QUEUE_LOCATION.getIRI() );

		RDFListFactory.getInstance().createQueue( jobsContainer );

		containerRepository.createChild( app.getIRI(), jobsContainer );
		aclRepository.createACL( jobsContainer.getIRI() );
	}

	private IRI generateJobsContainerIRI( App app ) {
		String appString = app.getIRI().stringValue();
		String jobsString = Vars.getInstance().getJobsContainer();
		return valueFactory.createIRI( appString + jobsString );
	}

	private void createLDAPServersContainer( App app ) {
		IRI containerIRI = generateLDAPServersContainerIRI( app );
		RDFResource ldapServersResource = new RDFResource( containerIRI );
		BasicContainer ldapServersContainer = BasicContainerFactory.getInstance().create( ldapServersResource );
		containerRepository.createChild( app.getIRI(), ldapServersContainer );
		aclRepository.createACL( ldapServersContainer.getIRI() );
	}

	private IRI generateLDAPServersContainerIRI( App app ) {
		String appString = app.getIRI().stringValue();
		String ldapServersString = Vars.getInstance().getAppLDAPServerContainer();
		return valueFactory.createIRI( appString + ldapServersString );
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
}
