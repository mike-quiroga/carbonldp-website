package com.carbonldp.test.jobs;

import com.carbonldp.jobs.BackupJobExecutor;
import com.carbonldp.test.AbstractIT;
import com.sun.deploy.net.proxy.ProxyUtils;
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
import java.util.stream.Stream;

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
