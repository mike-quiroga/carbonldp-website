package com.carbonldp.repository.updates;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.repository.SpringLocalRepositoryManager;
import com.carbonldp.sparql.SPARQLTemplate;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.Action;
import com.carbonldp.utils.ActionWithResult;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
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

	// TODO: Instead of loading a file, build the resources dynamically
	protected void loadResourcesFile( String resourcesFile, String baseURI ) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream( resourcesFile );

		transactionWrapper.runInPlatformContext( () -> {
			try {
				connectionFactory.getConnection().add( inputStream, baseURI, RDFFormat.TRIG );
			} catch ( IOException | RDFParseException | RepositoryException e ) {
				throw new RuntimeException( e );
			}
		} );

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