package com.carbonldp.apps;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceFactory;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

@Transactional
public class SesameAppService extends AbstractSesameLDPService implements AppService {
	private final AppRepository appRepository;

	private TransactionTemplate transactionTemplate;

	private AppService appService;

	@Inject
	public void setAppService( AppService appService ) {
		this.appService = appService;
	}

	@Autowired
	private void setTransactionTemplate( PlatformTransactionManager transactionManager ) {
		transactionTemplate = new TransactionTemplate( transactionManager );
	}

	public SesameAppService( RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRepository appRepository ) {
		super( sourceRepository, containerRepository, aclRepository );
		this.appRepository = appRepository;
	}

	@Override
	public App create( App app ) {
		final App createdApp = appRepository.create( app );
		appService.initialize( createdApp );
		/*
		transactionTemplate.execute( ( status ) -> {
			AppContextTemplate.runInAppContext( app, () -> initialize( createdApp ) );
			return null;
		} );
		*/
		return createdApp;
	}

	@Override
	@RunInAppContext
	@Transactional
	public void initialize( App app ) {
		BasicContainer rootContainer = createRootContainer( app );
		ACL rootContainerACL = createRootContainerACL( rootContainer );

		Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		aclRepository.grantPermissions( rootContainerACL, Arrays.asList( agent ), Arrays.asList(
			ACEDescription.Permission.READ
		) );

		// TODO: Create default resources in the Application's repository
		// -- TODO: Application Roles Container
		// -- TODO: ACLs
	}

	private BasicContainer createRootContainer( App app ) {
		RDFSource containerSource = RDFSourceFactory.create( app.getRootContainerURI() );
		BasicContainer rootContainer = BasicContainerFactory.create( containerSource );
		containerRepository.create( rootContainer );
		return rootContainer;
	}

	private ACL createRootContainerACL( BasicContainer rootContainer ) {
		return aclRepository.createACL( rootContainer.getDocument() );
	}
}
