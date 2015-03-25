package com.carbonldp.apps;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;

@Transactional
public class SesameAppService extends AbstractSesameLDPService implements AppService {
	private final AppRepository appRepository;
	private final AppRoleRepository appRoleRepository;

	public SesameAppService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRepository appRepository, AppRoleRepository appRoleRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		Assert.notNull( appRepository );
		this.appRepository = appRepository;
		Assert.notNull( appRoleRepository );
		this.appRoleRepository = appRoleRepository;
	}

	@Override
	public App create( App app ) {
		final App createdApp = appRepository.create( app );

		transactionWrapper.runInAppcontext( app, () -> initialize( createdApp ) );

		return createdApp;
	}

	/**
	 * Creates the initial resources of an app.
	 * <b>Needs to be called inside the app's context.</b>
	 *
	 * @param app
	 * 	The app to initialize
	 */
	private void initialize( App app ) {
		Container rootContainer = createRootContainer( app );
		ACL rootContainerACL = createRootContainerACL( rootContainer );

		Container appRolesContainer = appRoleRepository.createAppRolesContainer( rootContainer.getURI() );
		ACL appRolesContainerACL = createAppRolesContainerACL( appRolesContainer );

		AppRole appAdminRole = createAppAdminRole( appRolesContainer );
		ACL appAdminRoleACL = createAppAdminRoleACL( appAdminRole );

		addCurrentAgentToAppAdminRole( appAdminRole );

		addDefaultPermissions( appAdminRole, rootContainerACL );

		// TODO: Finish this
	}

	private BasicContainer createRootContainer( App app ) {
		BasicContainer rootContainer = BasicContainerFactory.create( new RDFResource( app.getRootContainerURI() ) );
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
		AppRole appAdminRole = AppRoleFactory.create( new RDFResource( appAdminRoleURI ) );
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

	private void addDefaultPermissions( AppRole appAdminRole, ACL rootContainerACL ) {
		aclRepository.grantPermissions( rootContainerACL, Arrays.asList( appAdminRole ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE,

			ACEDescription.Permission.CREATE_ACCESS_POINT,
			ACEDescription.Permission.CREATE_CHILD,
			ACEDescription.Permission.ADD_MEMBER
		) );
	}
}
