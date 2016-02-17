package com.carbonldp.repository.updates;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.SesameContainerRepository;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.repository.security.SecuredNativeStore;
import com.carbonldp.repository.txn.ApplicationContextConnectionFactory;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.sparql.SPARQLTemplate;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.Action;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.spring.RepositoryConnectionFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public abstract class AbstractUpdateAction extends AbstractComponent implements Action {
	protected ContainerRepository containerRepository;
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

	protected void emptyRepository( Repository repository ) {
		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "A connection couldn't be retrieved.", e );
		}

		try {
			connection.remove( (Resource) null, null, null );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The resources couldn't be loaded.", e );
		} finally {
			try {
				connection.close();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( "The connection couldn't be closed.", e );
			}
		}
	}

	// TODO: Instead of loading a file, build the resources dynamically
	protected void loadResourcesFile( Repository repository, String resourcesFile, String baseURI ) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream( resourcesFile );

		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "A connection couldn't be retrieved.", e );
		}

		try {
			connection.add( inputStream, baseURI, RDFFormat.TRIG );
		} catch ( RDFParseException e ) {
			throw new RuntimeException( "The file couldn't be parsed.", e );
		} catch ( RepositoryException | IOException e ) {
			throw new RuntimeException( "The resources couldn't be loaded.", e );
		} finally {
			try {
				connection.close();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( "The connection couldn't be closed.", e );
			}
		}
	}

	protected void executeSPARQLQuery( Repository repository, String queryString ) {
		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "A connection couldn't be retrieved.", e );
		}

		try {
			connection.prepareUpdate( QueryLanguage.SPARQL, queryString ).execute();
		} catch ( MalformedQueryException e ) {
			throw new RuntimeException( "The file couldn't be parsed.", e );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The resources couldn't be loaded.", e );
		} catch ( UpdateExecutionException e ) {
			throw new RuntimeException( "The update couldn't be executed.", e );
		} finally {
			try {
				connection.close();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( "The connection couldn't be closed.", e );
			}
		}
	}

	protected void closeRepository( Repository repository ) {
		try {
			repository.shutDown();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The repository couldn't be closed.", e );
		}
	}

	protected ConnectionRWTemplate getConnectionTemplate( Repository repository ) {
		SesameConnectionFactory factory = new RepositoryConnectionFactory( repository );
		return new ConnectionRWTemplate( factory );
	}

	protected Set<App> getAllApps() {
		return transactionWrapper.runInPlatformContext( () -> {
			Set<App> apps = new HashSet<>();
			URI platformAppsContainer = new URIImpl( Vars.getInstance().getHost() + Vars.getInstance().getMainContainer() + Vars.getInstance().getAppsContainer() );
			Set<Statement> membershipStatements = containerRepository.getMembershipTriples( platformAppsContainer );

			for ( Statement membershipStatement : membershipStatements ) {
				URI appURI = ValueUtil.getURI( membershipStatement.getObject() );
				App app = appRepository.get( appURI );
				apps.add( app );
			}
			return apps;
		} );

	}

	@Autowired
	public void setAppRepository( AppRepository appRepository ) { this.appRepository = appRepository; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {
		this.transactionWrapper = transactionWrapper;
	}

	@Autowired
	public void setSparqlTemplate( SPARQLTemplate sparqlTemplate ) { this.sparqlTemplate = sparqlTemplate; }
}
