package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.ldp.nonrdf.backup.BackupService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Random;

/**
 * @author NestorVenegas
 * @since version
 */
public class ExportBackupJobExecutor implements TypedJobExecutor {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private FileRepository fileRepository;
	private BackupService backupService;
	private TransactionWrapper transactionWrapper;
	private ExecutionService executionService;
	protected RDFSourceRepository sourceRepository;

	@Override
	public boolean supports( JobDescription.Type jobType ) {
		return jobType == JobDescription.Type.EXPORT_BACKUP_JOB;
	}

	@Override
	public void execute( App app, Job job, Execution execution ) {
		String appRepositoryID = app.getRepositoryID();
		String appRepositoryPath = Vars.getInstance().getAppsFilesDirectory().concat( Consts.SLASH ).concat( appRepositoryID );
		File nonRDFSourceDirectory = new File( appRepositoryPath );
		File rdfRepositoryFile = transactionWrapper.runInAppContext( app, () -> fileRepository.createAppRepositoryRDFFile() );
		File zipFile = nonRDFSourceDirectory.exists() ?
			fileRepository.createZipFile( nonRDFSourceDirectory, rdfRepositoryFile ) :
			fileRepository.createZipFile( rdfRepositoryFile );

		URI backupURI = createAppBackup( app.getURI(), zipFile );

		deleteTemporaryFile( zipFile );
		deleteTemporaryFile( rdfRepositoryFile );

		executionService.addResult( execution.getURI(), backupURI );
	}

	private URI createAppBackup( URI appURI, File zipFile ) {
		URI backupURI = createBackupURI( appURI );
		backupService.createAppBackup( appURI, backupURI, zipFile );

		return backupURI;
	}

	private URI createBackupURI( URI appURI ) {
		URI jobsContainerURI = new URIImpl( appURI.stringValue() + Vars.getInstance().getBackupsContainer() );
		URI backupURI;
		do {
			backupURI = new URIImpl( jobsContainerURI.stringValue().concat( createRandomSlug() ).concat( Consts.SLASH ) );
		} while ( sourceRepository.exists( backupURI ) );
		return backupURI;
	}

	private void deleteTemporaryFile( File file ) {
		boolean wasDeleted = false;
		try {
			wasDeleted = file.delete();
		} catch ( SecurityException e ) {
			LOG.warn( "A temporary file couldn't be deleted. Exception:", e );
		}
		if ( ! wasDeleted ) LOG.warn( "The temporary file: '{}', couldn't be deleted.", file.toString() );
	}

	private String createRandomSlug() {
		Random random = new Random();
		return String.valueOf( Math.abs( random.nextLong() ) );
	}

	@Autowired
	public void setBackupService( BackupService backupService ) { this.backupService = backupService; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setExecutionService( ExecutionService executionService ) { this.executionService = executionService; }

	@Autowired
	public void setRDFSourceRepository( RDFSourceRepository sourceRepository ) { this.sourceRepository = sourceRepository; }

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) {
		this.fileRepository = fileRepository;
	}
}