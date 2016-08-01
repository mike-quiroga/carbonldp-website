package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.ldp.nonrdf.backup.BackupService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RelativeNQuadsWriter;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
	private ExecutionService executionService;
	private RDFSourceRepository sourceRepository;
	private TransactionWrapper transactionWrapper;
	private ConnectionRWTemplate connectionTemplate;

	@Override
	public boolean supports( JobDescription.Type jobType ) {
		return jobType == JobDescription.Type.EXPORT_BACKUP_JOB;
	}

	@Override
	public void execute( App app, Job job, Execution execution ) {

		if ( ! job.hasType( ExportBackupJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ExportBackupJobDescription.Resource.CLASS.getIRI().stringValue() ) );

		Map<String, File> filesMap = new HashMap<>();
		String domainCode = UUID.randomUUID().toString();
		String appCode = UUID.randomUUID().toString();

		addNonRDFSourceDirectoryToEntries( filesMap, app.getRepositoryID() );
		addConfigFileToEntries( filesMap, domainCode, appCode );

		File rdfRepositoryFile = addRDFRepositoryFileToEntries( app, filesMap, domainCode, appCode );

		File zipFile = fileRepository.createZipFile( filesMap );

		IRI backupIRI = createAppBackup( app.getIRI(), zipFile );

		fileRepository.deleteFile( zipFile );
		fileRepository.deleteFile( rdfRepositoryFile );

		executionService.addResult( execution.getIRI(), backupIRI );
	}

	private void addNonRDFSourceDirectoryToEntries( Map<String, File> filesMap, String appRepositoryID ) {
		String appRepositoryPath = Vars.getInstance().getAppsFilesDirectory().concat( Consts.SLASH ).concat( appRepositoryID );
		File nonRDFSourceDirectory = new File( appRepositoryPath );
		if ( nonRDFSourceDirectory.exists() ) filesMap.put( Vars.getInstance().getAppDataDirectoryName(), nonRDFSourceDirectory );
	}

	private void addConfigFileToEntries( Map<String, File> filesMap, String domainPlaceholder, String appPlaceholder ) {
		String configFileName = Vars.getInstance().getBackupsConfigFile();

		Set<String> configFileData = new HashSet<>();
		configFileData.add( Vars.getInstance().getBackupsConfigDomainPlaceholder() + "=" + domainPlaceholder );
		configFileData.add( Vars.getInstance().getBackupsConfigAppPlaceholder() + "=" + appPlaceholder );

		File configFile = fileRepository.createTempFile( configFileData );
		filesMap.put( configFileName, configFile );
	}

	private File addRDFRepositoryFileToEntries( App app, Map<String, File> filesMap, String domainPlaceholder, String appPlaceholder ) {
		File rdfRepositoryFile = transactionWrapper.runInAppContext( app, () -> createAppRepositoryRDFFile( app, domainPlaceholder, appPlaceholder ) );
		String rdfRepositoryFileName = Vars.getInstance().getAppDataFileName() + Consts.PERIOD + RDFFormat.NQUADS.getDefaultFileExtension();

		filesMap.put( rdfRepositoryFileName, rdfRepositoryFile );
		return rdfRepositoryFile;
	}

	private File createAppRepositoryRDFFile( App app, String domainPlaceholder, String appPlaceholder ) {
		File temporaryFile;
		FileOutputStream outputStream = null;

		try {
			temporaryFile = fileRepository.createTempFile();

			outputStream = new FileOutputStream( temporaryFile );

			String appSlug = app.getIRI().stringValue();
			appSlug = appSlug.substring( Vars.getInstance().getAppsContainerURL().length() );

			RelativeNQuadsWriter nQuadsWriter = new RelativeNQuadsWriter( outputStream, domainPlaceholder, appSlug, appPlaceholder );

			connectionTemplate.write( connection -> connection.export( nQuadsWriter ) );
		} catch ( IOException | SecurityException e ) {
			throw new RuntimeException( "The temporary file couldn't be created. Exception:", e );
		} finally {
			try {
				if ( outputStream != null ) outputStream.close();
			} catch ( IOException e ) {
				LOG.warn( "The outputStream couldn't be closed. Exception: ", e );
			}
		}

		return temporaryFile;
	}

	private IRI createAppBackup( IRI appIRI, File zipFile ) {
		IRI backupIRI = fileRepository.createBackupIRI( appIRI );
		backupService.createAppBackup( appIRI, backupIRI, zipFile );

		return backupIRI;
	}

	@Autowired
	public void setBackupService( BackupService backupService ) { this.backupService = backupService; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) { this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setConnectionTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}

	@Autowired
	public void setExecutionService( ExecutionService executionService ) { this.executionService = executionService; }

	@Autowired
	public void setRDFSourceRepository( RDFSourceRepository sourceRepository ) { this.sourceRepository = sourceRepository; }

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) { this.fileRepository = fileRepository; }
}
