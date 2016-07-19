package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.ldp.nonrdf.backup.BackupService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.*;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.33.0
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

		if ( ! job.hasType( ExportBackupJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ExportBackupJobDescription.Resource.CLASS.getIRI().stringValue() ) );

		Map<File, String> entries = new HashMap<>();
		String domainCode = UUID.randomUUID().toString();
		String appCode = UUID.randomUUID().toString();

		addNonRDFSourceDirectoryToEntries( entries, app.getRepositoryID() );
		addConfigFileToEntries( entries, domainCode, appCode );
		File rdfRepositoryFile = addRDFRepositoryFileToEntries( app, entries, domainCode, appCode );

		File zipFile = fileRepository.createZipFile( entries );

		IRI backupIRI = createAppBackup( app.getIRI(), zipFile );

		fileRepository.deleteFile( zipFile );
		fileRepository.deleteFile( rdfRepositoryFile );

		executionService.addResult( execution.getIRI(), backupIRI );
	}

	private void addNonRDFSourceDirectoryToEntries( Map<File, String> entries, String appRepositoryID ) {
		String appRepositoryPath = Vars.getInstance().getAppsFilesDirectory().concat( Consts.SLASH ).concat( appRepositoryID );
		File nonRDFSourceDirectory = new File( appRepositoryPath );
		if ( nonRDFSourceDirectory.exists() ) entries.put( nonRDFSourceDirectory, Vars.getInstance().getAppDataDirectoryName() );
	}

	private void addConfigFileToEntries( Map<File, String> entries, String domainCode, String appCode ) {
		String configFileName = Vars.getInstance().getBackupsConfigFile();
		Set<String> configFileData = new HashSet<>();
		configFileData.add( Vars.getInstance().getBackupsConfigDomainCode() + " = " + domainCode );
		configFileData.add( Vars.getInstance().getBackupsConfigAppCode() + " = " + appCode );
		File configFile = fileRepository.createTempFile( configFileData );
		entries.put( configFile, configFileName );
	}

	private File addRDFRepositoryFileToEntries( App app, Map<File, String> entries, String domainCode, String appCode ) {
		File rdfRepositoryFile = transactionWrapper.runInAppContext( app, () -> fileRepository.createAppRepositoryRDFFile( domainCode, appCode ) );
		entries.put( rdfRepositoryFile, Vars.getInstance().getAppDataFileName() + Consts.PERIOD + RDFFormat.NQUADS.getDefaultFileExtension() );
		return rdfRepositoryFile;
	}

	private IRI createAppBackup( IRI appIRI, File zipFile ) {
		IRI backupIRI = fileRepository.createBackupIRI( appIRI );
		backupService.createAppBackup( appIRI, backupIRI, zipFile );

		return backupIRI;
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
