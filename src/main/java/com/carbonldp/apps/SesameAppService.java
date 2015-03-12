package com.carbonldp.apps;

import com.carbonldp.Vars;
import com.carbonldp.agents.Agent;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceFactory;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Transactional
public class SesameAppService extends AbstractSesameLDPService implements AppService {
	private final AppRepository appRepository;

	public SesameAppService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRepository appRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.appRepository = appRepository;
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
		Container appRolesContainer = createAppRolesContainer( rootContainer.getURI() );
		ACL appRolesContainerACL = creatAppRolesContainerACL( appRolesContainer );
		AppRole appAdminRole = createAppAdminRole( appRolesContainer );
		// TODO: Create appRoles container
		// TODO: Create app-admin appRole
		// TODO:

		// TODO: Remove this, the main appRole should be the subject
		Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		aclRepository.grantPermissions( rootContainerACL, Arrays.asList( agent ), Arrays.asList(
			ACEDescription.Permission.READ
		) );
	}

	private BasicContainer createRootContainer( App app ) {
		RDFSource containerSource = RDFSourceFactory.create( app.getRootContainerURI() );
		BasicContainer rootContainer = BasicContainerFactory.create( containerSource );
		containerRepository.create( rootContainer );
		return rootContainer;
	}

	private ACL createRootContainerACL( Container rootContainer ) {
		return aclRepository.createACL( rootContainer.getDocument() );
	}

	private BasicContainer createAppRolesContainer( URI rootContainerURI ) {
		URI appRolesContainerURI = createAppRolesContainerURI( rootContainerURI );
		RDFResource resource = new RDFResource( new LinkedHashModel(), appRolesContainerURI );
		BasicContainer appRolesContainer = BasicContainerFactory.create( resource );
		containerRepository.createChild( rootContainerURI, appRolesContainer );
		return appRolesContainer;
	}

	private ACL creatAppRolesContainerACL( Container appRolesContainer ) {
		return aclRepository.createACL( appRolesContainer.getDocument() );
	}

	private URI createAppRolesContainerURI( URI rootContainerURI ) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( rootContainerURI )
				  .append( Vars.getAppRolesContainer() )
		;
		return new URIImpl( uriBuilder.toString() );
	}

	private AppRole createAppAdminRole( Container appRolesContainer ) {
		// TODO
		return null;
	}
}
