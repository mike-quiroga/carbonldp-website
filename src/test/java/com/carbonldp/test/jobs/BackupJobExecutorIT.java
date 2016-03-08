package com.carbonldp.test.jobs;

import com.carbonldp.jobs.BackupJobExecutor;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.Model;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class BackupJobExecutorIT extends AbstractIT {

	@Test
	public void createTemporaryRDFBackupFileIT() {
		File rdfRepositoryFile = transactionWrapper.runInAppContext( app, () -> {
			try {
				Method privateMethod = BackupJobExecutor.class.getDeclaredMethod( "createTemporaryRDFBackupFile" );
				privateMethod.setAccessible( true );
				return (File) privateMethod.invoke( backupJobExecutor );

			} catch ( NoSuchMethodException e ) {
				throw new SkipException( "can't find the method", e );
			} catch ( InvocationTargetException e ) {
				throw new SkipException( "Exception inside the method", e );
			} catch ( IllegalAccessException e ) {
				throw new SkipException( "can't access the method", e );
			}
		} );

		String appString = "";

		try {
			Files.lines( rdfRepositoryFile.toPath() ).forEachOrdered( str -> appString.concat( str ) );
		} catch ( IOException e ) {
			throw new SkipException( "unable to read file", e );
		}
		Model appModel = getBody( appString );
	}

	private Model getBody( String appString ) {

		Reader reader = new CharArrayReader(
			appString.toCharArray()
		);
		RDFParser rdfParser = Rio.createParser( RDFFormat.TRIG );
		AbstractModel model = new LinkedHashModel();
		rdfParser.setRDFHandler( new StatementCollector( model ) );
		try {
			rdfParser.parse( reader, "" );
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
		return model;
	}
}
