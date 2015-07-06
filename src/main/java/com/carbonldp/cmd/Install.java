package com.carbonldp.cmd;

import com.carbonldp.utils.PropertiesUtil;
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
import java.util.Properties;

public class Install {

	public static String propertiesFile = "local-config.properties";
	public static String defaultResourcesFile = "platform-default.trig";

	public static void main( String[] args ) {
		Install install = new Install();
		install.execute();
	}

	private Properties properties;

	private Install() {
		this.properties = readPropertiesFile( propertiesFile );
	}

	private void execute() {
		Repository platformRepository = getRepository( this.properties.getProperty( "repositories.platform.sesame.directory" ) );
		emptyRepository( platformRepository );
		loadDefaultResourcesfile( platformRepository, defaultResourcesFile, this.properties.getProperty( "platform.url" ) );
	}

	private Properties readPropertiesFile( String propertiesFile ) {
		Properties properties = new Properties();
		InputStream inputStream = null;

		inputStream = getClass().getClassLoader().getResourceAsStream( propertiesFile );

		if ( inputStream == null ) {
			throw new RuntimeException( "The property file: '" + propertiesFile + "', wasn't be found." );
		}

		try {
			properties.load( inputStream );
		} catch ( IOException e ) {
			throw new RuntimeException( "The properties coulnd't be loaded from the file: '" + propertiesFile + "'", e );
		}

		PropertiesUtil.resolveProperties( properties );

		return properties;
	}

	private Repository getRepository( String repositoryFile ) {
		File repositoryDir = new File( repositoryFile );
		Repository repository = new SailRepository( new NativeStore( repositoryDir ) );
		try {
			repository.initialize();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "The repository in the directory: '" + repositoryFile + "', couldn't be initialized.", e );
		}
		return repository;
	}

	private void emptyRepository( Repository repository ) {
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
	private void loadDefaultResourcesfile( Repository repository, String resourcesFile, String baseURI ) {
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
}
