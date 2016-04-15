package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.ldp.nonrdf.backup.BackupService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.IRIUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class ExportBackupJobExecutor implements TypedJobExecutor {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private FileRepository fileRepository;
	private BackupService backupService;
	private TransactionWrapper transactionWrapper;
	private ExecutionService executionService;
	protected RDFSourceRepository sourceRepository;
	private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

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

		IRI backupIRI = createAppBackup( app.getIRI(), zipFile );

		deleteTemporaryFile( zipFile );
		deleteTemporaryFile( rdfRepositoryFile );

		executionService.addResult( execution.getIRI(), backupIRI );
	}

	private IRI createAppBackup( IRI appIRI, File zipFile ) {
		IRI backupIRI = createBackupIRI( appIRI );
		backupService.createAppBackup( appIRI, backupIRI, zipFile );

		return backupIRI;
	}

	private IRI createBackupIRI( IRI appIRI ) {
		IRI jobsContainerIRI = valueFactory.createIRI( appIRI.stringValue() + Vars.getInstance().getBackupsContainer() );
		IRI backupIRI;
		do {
			backupIRI = valueFactory.createIRI( jobsContainerIRI.stringValue().concat(  IRIUtil.createRandomSlug() ).concat( Consts.SLASH ) );
		} while ( sourceRepository.exists( backupIRI ) );
		return backupIRI;
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
