package com.carbonldp.repository.updates;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.config.ConfigurationConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.repository.security.SecuredNativeStore;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.sparql.SPARQLTemplate;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.Action;
import com.carbonldp.utils.ValueUtil;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.spring.RepositoryConnectionFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public abstract class AbstractUpdateAction extends AbstractComponent implements Action {
	protected ContainerRepository containerRepository;
	protected SesameConnectionFactory connectionFactory;
	protected AppRepository appRepository;
	protected TransactionWrapper transactionWrapper;
	protected SPARQLTemplate sparqlTemplate;

	public void run() {
		try {
			this.execute();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	protected abstract void execute() throws Exception;

	protected Repository getRepository( String repositoryFile ) {
		File repositoryDir = new File( repositoryFile );
		Repository repository = new SailRepository( new SecuredNativeStore( repositoryDir ) );
		try {
			repository.initialize();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The repository in the directory: '" + repositoryFile + "', couldn't be initialized.", e );
		}
		return repository;
	}

	protected RepositoryConnection getConnection( Repository repository ) {
		try {
			return repository.getConnection();
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( e );
		}
	}

	// TODO: Instead of loading a file, build the resources dynamically
	protected void loadResourcesFile( String resourcesFile, String baseURI ) {
		Map<String, Value> values = new HashMap<>();

		values.put( "baseURI", new URIImpl( baseURI ) );
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( "LOAD <" +getClass().getClassLoader().getResource( resourcesFile ).toString()+ ">", null ) );

	}


	protected Set<App> getAllApps() {
		return transactionWrapper.runInPlatformContext( () -> {
			Set<App> apps = new HashSet<>();
			URI platformAppsContainer = new URIImpl( Vars.getInstance().getHost() + Vars.getInstance().getMainContainer() + Vars.getInstance().getAppsContainer() );
			Set<URI> appURIs = containerRepository.getContainedURIs( platformAppsContainer );

			for ( URI appURI : appURIs ) {
				App app = appRepository.get( appURI );
				apps.add( app );
			}
			return apps;
		} );
	}

	public void setBeans( AnnotationConfigApplicationContext context ) {
		transactionWrapper = context.getBean( TransactionWrapper.class );
		connectionFactory = context.getBean( SesameConnectionFactory.class );
		sparqlTemplate = new SPARQLTemplate( connectionFactory );
		containerRepository = context.getBean( ContainerRepository.class );
		appRepository = context.getBean( AppRepository.class );

	}
}
