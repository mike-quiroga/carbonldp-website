package com.carbonldp.cmd;

import com.carbonldp.repository.security.SecuredNativeStore;
import com.carbonldp.utils.PropertiesUtil;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Install {
	public static String propertiesFile = "-config.properties";
	public static String defaultResourcesFile = "platform-default.trig";
	private static Map<String, String> defaultArguments = new HashMap<String, String>() {{
		put( "env", "local" );
	}};

	public static void main( String[] args ) {
		Map<String, String> arguments = parseArguments( args );

		Install install = new Install( arguments );

		install.execute();
	}

	private static Map<String, String> parseArguments( String[] args ) {
		Map<String, String> arguments = new HashMap<>( Install.defaultArguments );

		for ( int i = 0, length = args.length; i < length; i++ ) {
			String argument = args[i];
			if ( Install.isKey( argument ) ) {
				String key = Install.getArgumentKey( argument );
				String value = Install.getArgumentValue( args[++ i] );
				arguments.put( key, value );
			} else if ( Install.isFlag( argument ) ) {
				String flag = Install.getArgumentFlag( argument );
				arguments.put( flag, "true" );
			}
		}

		return arguments;
	}

	private static String getArgumentFlag( String argument ) {
		if ( argument.length() <= 2 ) throw new IllegalArgumentException( "A name needs to be specified when adding a flag." );
		return argument.substring( 2 );
	}

	private static String getArgumentKey( String argument ) {
		if ( argument.length() <= 1 ) throw new IllegalArgumentException( "The key of an argument cannot be empty." );
		return argument.substring( 1 );
	}

	private static String getArgumentValue( String arg ) {
		if ( arg.startsWith( "-" ) ) throw new IllegalArgumentException( "An argument value cannot start with '-'." );

		return arg;
	}

	private static boolean isKey( String argument ) {
		return argument.startsWith( "-" ) && ! argument.startsWith( "--" );
	}

	private static boolean isFlag( String argument ) {
		return argument.startsWith( "--" );
	}

	private Properties properties;
	private String environment;

	private Install( Map<String, String> arguments ) {
		this.setEnvironment( arguments.get( "env" ) );
		this.properties = readPropertiesFile( this.environment.concat( Install.propertiesFile ) );
	}

	private void execute() {
		Repository platformRepository = getRepository( this.properties.getProperty( "repositories.platform.sesame.directory" ) );
		emptyRepository( platformRepository );
		loadDefaultResourcesfile( platformRepository, defaultResourcesFile, this.properties.getProperty( "platform.url" ) );
	}

	private Properties readPropertiesFile( String propertiesFile ) {
		Properties properties = new Properties();
		InputStream inputStream;

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
		Repository repository = new SailRepository( new SecuredNativeStore( repositoryDir ) );
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
	private void loadDefaultResourcesfile( Repository repository, String resourcesFile, String baseIRI ) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream( resourcesFile );

		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( "A connection couldn't be retrieved.", e );
		}

		try {
			connection.add( inputStream, baseIRI, RDFFormat.TRIG );
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

	public void setEnvironment( String environment ) {
		this.environment = environment;
	}
}
