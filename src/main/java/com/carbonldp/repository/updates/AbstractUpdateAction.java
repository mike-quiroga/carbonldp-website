package com.carbonldp.repository.updates;

import com.carbonldp.AbstractComponent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.AgentService;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authentication.token.app.AppTokenRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFMapRepository;
import com.carbonldp.sparql.SPARQLTemplate;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.Action;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public abstract class AbstractUpdateAction extends AbstractComponent implements Action {
	protected ContainerRepository containerRepository;
	protected AppTokenRepository appTokensRepository;
	protected SesameConnectionFactory connectionFactory;
	protected AppRepository appRepository;
	protected TransactionWrapper transactionWrapper;
	protected SPARQLTemplate sparqlTemplate;
	protected RDFSourceRepository sourceRepository;
	protected ACLRepository aclRepository;
	protected RDFDocumentRepository documentRepository;
	protected AgentRepository platformAgentRepository;
	protected AgentService platformAgentService;
	protected RDFSourceService sourceService;
	protected AppRoleRepository appRoleRepository;
	protected ContainerService containerService;
	protected RDFMapRepository mapRepository;
	protected static ValueFactory valueFactory = SimpleValueFactory.getInstance();

	public void run() {
		try {
			this.execute();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	protected abstract void execute() throws Exception;

	// TODO: Instead of loading a file, build the resources dynamically
	protected void loadResourcesFile( String resourcesFile, String baseIRI ) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream( resourcesFile );

		transactionWrapper.runInPlatformContext( () -> {
			try {
				connectionFactory.getConnection().add( inputStream, baseIRI, RDFFormat.TRIG );
			} catch ( IOException | RDFParseException | RepositoryException e ) {
				throw new RuntimeException( e );
			}
		} );

	}

	protected Set<App> getAllApps() {
		return transactionWrapper.runInPlatformContext( () -> appRepository.getAll() );
	}

	public void setBeans( AnnotationConfigApplicationContext context ) {
		transactionWrapper = context.getBean( TransactionWrapper.class );
		connectionFactory = context.getBean( SesameConnectionFactory.class );
		sparqlTemplate = new SPARQLTemplate( connectionFactory );
		containerRepository = context.getBean( ContainerRepository.class );
		appRepository = context.getBean( AppRepository.class );
		sourceRepository = context.getBean( RDFSourceRepository.class );
		aclRepository = context.getBean( ACLRepository.class );
		documentRepository = context.getBean( RDFDocumentRepository.class );
		appTokensRepository = context.getBean( AppTokenRepository.class );
		platformAgentRepository = context.getBean( "platformAgentRepository", AgentRepository.class );
		platformAgentService = context.getBean( "platformAgentService", AgentService.class );
		sourceService = context.getBean( RDFSourceService.class );
		appRoleRepository = context.getBean( AppRoleRepository.class );
		containerService = context.getBean( ContainerService.class );
		mapRepository = context.getBean( RDFMapRepository.class );
	}
}