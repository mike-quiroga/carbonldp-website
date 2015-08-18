package com.carbonldp.repository.updates;

import com.carbonldp.utils.Action;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public abstract class AbstractUpdateAction implements Action {
	protected Repository getRepository( String repositoryFile ) {
		File repositoryDir = new File( repositoryFile );
		Repository repository = new SailRepository( new NativeStore( repositoryDir ) );
		try {
			repository.initialize();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The repository in the directory: '" + repositoryFile + "', couldn't be initialized.", e );
		}
		return repository;
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

	protected void closeRepository( Repository repository ) {
		try {
			repository.shutDown();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The repository couldn't be closed.", e );
		}
	}
}
