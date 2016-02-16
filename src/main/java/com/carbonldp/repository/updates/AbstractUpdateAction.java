package com.carbonldp.repository.updates;

import com.carbonldp.AbstractComponent;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.repository.security.SecuredNativeStore;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.Action;
import org.openrdf.model.Resource;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public abstract class AbstractUpdateAction extends AbstractComponent implements Action {
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
}
