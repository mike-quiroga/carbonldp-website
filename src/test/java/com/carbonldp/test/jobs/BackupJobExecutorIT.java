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
import org.springframework.aop.framework.Advised;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.zip.ZipFile;

import static org.testng.Assert.*;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class BackupJobExecutorIT extends AbstractIT {
	public String appString = "";

	@Test
	public void createTemporaryRDFBackupFileIT() {
		BackupJobExecutor backupJobExecutor;
		try {
			backupJobExecutor = (BackupJobExecutor) ( (Advised) this.backupJobExecutor ).getTargetSource().getTarget();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}

		File rdfRepositoryFile = transactionWrapper.runInAppContext( app, () -> ReflectionTestUtils.invokeMethod( backupJobExecutor, "createTemporaryRDFBackupFile" ) );

		try {
			Files.lines( rdfRepositoryFile.toPath() ).forEachOrdered( str -> appString += str );
		} catch ( IOException e ) {
			throw new SkipException( "unable to read file", e );
		}
		Model appModel = getBody( appString );
	}

	@Test
	public void addFileToZipIT() {
		BackupJobExecutor backupJobExecutor;
		try {
			backupJobExecutor = (BackupJobExecutor) ( (Advised) this.backupJobExecutor ).getTargetSource().getTarget();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}

		File rdfRepositoryFile = transactionWrapper.runInAppContext( app, () -> ReflectionTestUtils.invokeMethod( backupJobExecutor, "createTemporaryRDFBackupFile" ) );
		File[] args = new File[1];
		args[0] = rdfRepositoryFile;
		File file = ReflectionTestUtils.invokeMethod( backupJobExecutor, "createZipFile", (Object) args );

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile( file );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		assertEquals( zipFile.size(), 1 );
		assertEquals( zipFile.entries().nextElement().getName(), args[0].getName() );
	}

	private Model getBody( String appString ) {

		Reader reader = new CharArrayReader(
			appString.toCharArray()
		);
		RDFParser rdfParser = Rio.createParser( RDFFormat.TRIG );
		AbstractModel model = new LinkedHashModel();
		rdfParser.setRDFHandler( new StatementCollector( model ) );
		try {
			rdfParser.parse( reader, app.getURI().stringValue() );
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
		return model;
	}
}
