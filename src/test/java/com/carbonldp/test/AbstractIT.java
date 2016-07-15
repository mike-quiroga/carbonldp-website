package com.carbonldp.test;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppFactory;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppService;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.ACLService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.repository.security.SecuredNativeStoreFactory;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.PropertiesUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.config.SailRegistry;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Test( groups = "integration-tests" )
@ContextConfiguration( classes = TestConfig.class )
public abstract class AbstractIT extends AbstractTestNGSpringContextTests {

	@Autowired
	protected AppService appService;
	@Autowired
	protected AppRepository appRepository;
	@Autowired
	protected PlatformContextActionTemplate platformContextTemplate;
	@Autowired
	protected ApplicationContextActionTemplate applicationContextTemplate;
	@Autowired
	@Qualifier( "platformAgentUsernamePasswordAuthenticationProvider" )
	protected AuthenticationProvider sesameUsernamePasswordAuthenticationProvider;
	@Autowired
	protected ACLService aclService;
	@Autowired
	protected RDFSourceRepository sourceRepository;
	@Autowired
	protected ACLRepository aclRepository;
	@Autowired
	protected TransactionWrapper transactionWrapper;
	@Autowired
	protected SesameConnectionFactory connectionFactory;
	@Autowired
	protected FileRepository fileRepository;

	protected final String testRepositoryID = "test-blog";
	protected final String testResourceIRI = "https://local.carbonldp.com/apps/test-blog/";

	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	protected static ValueFactory valueFactory = SimpleValueFactory.getInstance();

	private final String propertiesFile = "config.properties";
	private final String platformRepositoryLocationProperty = "repositories.platform.sesame.directory";
	private final String appsRepositoryLocationProperty = "repositories.apps.sesame.directory";
	private final String appsRepositoryFilesLocationProperty = "repositories.apps.files.directory";
	private final String platformRepositoryFilesLocationProperty = "repositories.platform.files.directory";

	private final String testDataLocation = "app-model-test.trig";
	private final String platformDefaultDataLocation = "platform-default.trig";

	protected Properties properties;
	protected App app;

	public AbstractIT() {
		SailRegistry.getInstance().add( new SecuredNativeStoreFactory() );
		this.properties = loadProperties( propertiesFile );
		PropertiesUtil.resolveProperties( properties );
		if ( ! Vars.hasBeenInitialized() ) Vars.initialize( properties );
		erasePlatformRepositoryDirectory( properties );
		eraseAppsRepositoryDirectory( properties );
		loadPlatformRepositoryDefaultData( properties );
		createRepositoryFilesDirectory( appsRepositoryFilesLocationProperty );
		createRepositoryFilesDirectory( platformRepositoryFilesLocationProperty );

	}

	private void createRepositoryFilesDirectory( String appsRepositoryFilesLocationProperty ) {
		File dir = new File( properties.getProperty( appsRepositoryFilesLocationProperty ) );
	}

	private Properties loadProperties( String fileName ) {
		Properties properties = new Properties();
		InputStream inputTest = getClass().getClassLoader().getResourceAsStream( fileName );
		InputStream input = getClass().getClassLoader().getResourceAsStream( "local-config.properties" );

		if ( input == null ) throw new RuntimeException( "Properties file not found" );
		if ( inputTest == null ) throw new RuntimeException( "Properties file not found" );

		try {
			properties.load( input );
			properties.load( inputTest );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		} finally {
			try {
				input.close();
			} catch ( IOException e ) {
				throw new RuntimeException( e );
			}
		}

		return properties;
	}

	private void erasePlatformRepositoryDirectory( Properties properties ) {
		String platformRepositoryLocation = properties.getProperty( platformRepositoryLocationProperty );
		try {
			deleteDirectory( platformRepositoryLocation );
		} catch ( IOException e ) {
			throw new RuntimeException( "The platform repository directory coulnd't be deleted.", e );
		}
	}

	private void eraseAppsRepositoryDirectory( Properties properties ) {
		String appsRepositoryLocation = properties.getProperty( appsRepositoryLocationProperty );
		try {
			deleteDirectory( appsRepositoryLocation );
		} catch ( IOException e ) {
			throw new RuntimeException( "The apps repository directory coulnd't be deleted.", e );
		}
	}

	private void loadPlatformRepositoryDefaultData( Properties properties ) {
		Repository platformRepository = getRepository( properties.getProperty( platformRepositoryLocationProperty ) );
		loadDefaultResourcesfile( platformRepository, platformDefaultDataLocation, properties.getProperty( "platform.url" ) );
		shutdownRepository( platformRepository );
	}

	private void deleteDirectory( String directoryLocation ) throws IOException {
		File directory = new File( directoryLocation );
		deleteDirectory( directory );
	}

	private void deleteDirectory( File directory ) throws IOException {
		if ( ! directory.isDirectory() ) {
			return;
		}

		if ( directory.list().length != 0 ) {
			String files[] = directory.list();

			for ( String temp : files ) {
				File fileToDelete = new File( directory, temp );

				if ( fileToDelete.isDirectory() ) deleteDirectory( fileToDelete );
				else deleteFile( fileToDelete );
			}
		}

		directory.delete();
	}

	private void deleteFile( File file ) throws IOException {
		file.delete();
	}

	private Repository getRepository( String repositoryFile ) {
		File repositoryDir = new File( repositoryFile );
		Repository repository = new SailRepository( new NativeStore( repositoryDir ) );
		try {
			repository.initialize();
		} catch ( RepositoryException e ) {
			LOG.debug( e.getMessage() );
			throw new RuntimeException( "The repository in the directory: '" + repositoryFile + "', couldn't be initialized.", e );
		}
		return repository;
	}

	private void shutdownRepository( Repository repository ) {
		try {
			repository.shutDown();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

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

	@BeforeClass( alwaysRun = true, dependsOnMethods = "springTestContextPrepareTestInstance" )
	public void setRepository() {

		ValueFactory factory = SimpleValueFactory.getInstance();
		RDFParser rdfParser = Rio.createParser( RDFFormat.TRIG );
		AbstractModel model = new LinkedHashModel();
		rdfParser.setRDFHandler( new StatementCollector( model ) );
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream( testDataLocation );
		try {
			rdfParser.parse( inputStream, testDataLocation );
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
		IRI appIRI = factory.createIRI( testResourceIRI );
		Container container = new BasicContainer( model, appIRI );
		App app = AppFactory.getInstance().create( container, appIRI.stringValue(), testRepositoryID );
		Authentication authentication = Mockito.mock( Authentication.class );

		Mockito.when( authentication.getPrincipal() ).thenReturn( "admin@carbonldp.com" );
		Mockito.when( authentication.getCredentials() ).thenReturn( "hello" );
		Authentication authToken;
		try {
			authToken = sesameUsernamePasswordAuthenticationProvider.authenticate( authentication );
		} catch ( BadCredentialsException e ) {
			throw new RuntimeException( e );
		}
		SecurityContextHolder.getContext().setAuthentication( authToken );

		// This if is needed here, lines above this are necessary to run every time before each class.
		if ( ! appService.exists( SimpleValueFactory.getInstance().createIRI( testResourceIRI ) ) )
			appService.create( app );
		this.app = transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> appService.get( valueFactory.createIRI( testResourceIRI ) ) );
	}
}
