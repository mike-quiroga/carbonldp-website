package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.config.ConfigurationConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.repository.updates.*;
import com.carbonldp.sparql.SPARQLTemplate;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.Action;
import com.google.common.io.Files;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class RepositoriesUpdater extends AbstractComponent {
	private static final String versionFileName = "version";
	private static final String versionFileCharset = "UTF-8";

	private static Map<RepositoryVersion, Action> versionsUpdates = new HashMap<RepositoryVersion, Action>() {{
		put( new RepositoryVersion( "1.0.0" ), new UpdateAction1o0o0() );
		put( new RepositoryVersion( "1.1.0" ), new UpdateAction1o1o0() );
		put( new RepositoryVersion( "1.2.0" ), new UpdateAction1o2o0() );
		put( new RepositoryVersion( "1.3.0" ), new UpdateAction1o3o0() );
	}};

	public boolean repositoriesAreUpToDate() {
		RepositoryVersion latestVersion = getLatestVersion();
		RepositoryVersion currentVersion = getCurrentVersion();
		return currentVersion.equals( latestVersion );
	}

	public void updateRepositories() {
		RepositoryVersion currentVersion = getCurrentVersion();
		AnnotationConfigApplicationContext context = initializeContext();

		RepositoriesUpdater.versionsUpdates
			.keySet()
			.stream()
			.filter( v -> currentVersion.compareTo( v ) < 0 )
			.sorted()
			.forEach( v -> {
				LOG.debug( "-- updateRepositories() - Running update of repository version: '{}'...", v );
				Action action = RepositoriesUpdater.versionsUpdates.get( v );
				( (AbstractUpdateAction) action ).setBeans( context );
				action.run();
				LOG.debug( "-- updateRepositories() - Repository update to version: '{}', complete.", v );
			} );

		context.close();
		setCurrentVersion( getLatestVersion() );
	}

	protected AnnotationConfigApplicationContext initializeContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
			TxnConfig.class,
			ConfigurationConfig.class,
			RepositoriesConfig.class,
			AppContextConfig.class
		);
		return context;
	}

	private RepositoryVersion getLatestVersion() {
		return RepositoriesUpdater.versionsUpdates
			.keySet()
			.stream()
			.sorted()
			.reduce( ( a, b ) -> b )
			.get();
	}

	private RepositoryVersion getCurrentVersion() {
		File versionFile = getVersionFile();

		if ( versionFile.exists() ) return getVersionFromFile( versionFile );
		return new RepositoryVersion( "0.0.0" );
	}

	private RepositoryVersion getVersionFromFile( File versionFile ) {
		String versionString;

		try {
			versionString = Files.readFirstLine( versionFile, Charset.forName( versionFileCharset ) );
		} catch ( IOException e ) {
			throw new RuntimeException( "The repository version file couldn't be read.", e );
		}

		try {
			return new RepositoryVersion( versionString );
		} catch ( IllegalArgumentException e ) {
			throw new RuntimeException( "The repository version file is corrupted.", e );
		}
	}

	private void setCurrentVersion( RepositoryVersion repositoryVersion ) {
		File versionFile = getVersionFile();

		if ( versionFile.exists() && ! versionFile.delete() ) throw new RuntimeException( "The repository version file couldn't be deleted." );

		try {
			Files.write( repositoryVersion.toString(), versionFile, Charset.forName( versionFileCharset ) );
		} catch ( IOException e ) {
			throw new RuntimeException( "The repository version file couldn't be written.", e );
		}
	}

	private File getVersionFile() {
		String versionFileDir = Vars.getInstance().getRepositoriesDirectory() + Consts.SLASH + RepositoriesUpdater.versionFileName;
		return new File( versionFileDir );
	}
}
