package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.ldp.nonrdf.RDFRepresentationDescription;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.ZipUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.IRI;
import org.openrdf.rio.RDFFormat;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public class ImportBackupJobExecutor implements TypedJobExecutor {
	private NonRDFSourceService nonRDFSourceService;
	private RDFSourceService sourceService;
	private ConnectionRWTemplate connectionTemplate;
	private TransactionWrapper transactionWrapper;
	private FileRepository fileRepository;

	@Override
	public boolean supports( JobDescription.Type jobType ) {
		return jobType == JobDescription.Type.IMPORT_BACKUP_JOB;
	}

	@Override
	public void execute( App app, Job job, Execution execution ) {
		if ( ! job.hasType( ImportBackupJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ImportBackupJobDescription.Resource.CLASS.getIRI().stringValue() ) );

		ImportBackupJob importBackupJob = new ImportBackupJob( job );
		IRI backupIRI = importBackupJob.getBackup();
		RDFRepresentation backupRDFRepresentation = new RDFRepresentation( sourceService.get( backupIRI ) );

		String mediaType = backupRDFRepresentation.getMediaType();

		if ( ! mediaType.equals( Consts.ZIP ) ) throw new JobException( new Infraction( 0x2005, "property", RDFRepresentationDescription.Property.MEDIA_TYPE.getIRI().stringValue() ) );

		File backupFile = nonRDFSourceService.getResource( backupRDFRepresentation );

		transactionWrapper.runInAppContext( app, () -> {
			replaceApp( backupFile );
			replaceAppFilesDirectory( backupFile );
		} );

	}

	private void replaceAppFilesDirectory( File backupFile ) {
		byte[] buffer = new byte[1024];
		int length;

		String filesDirectory = getFilesDirectory();
		File directory = new File( filesDirectory );
		fileRepository.deleteDirectory( directory );
		directory.mkdirs();

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile( backupFile );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while ( entries.hasMoreElements() ) {
			ZipEntry zipEntry = entries.nextElement();
			if ( zipEntry.isDirectory() ) continue;
			String fileName = zipEntry.getName();
			int slashIndex = fileName.indexOf( Consts.SLASH );
			if ( slashIndex == - 1 ) continue;
			fileName = fileName.substring( slashIndex );

			InputStream InputStream = null;
			try {
				InputStream = zipFile.getInputStream( zipEntry );
			} catch ( IOException e ) {
				throw new RuntimeException( e );
			}

			File newFile = new File( directory + fileName );
			File parentsFile = newFile.getParentFile();
			parentsFile.mkdirs();

			FileOutputStream fileOutputStream;
			try {
				fileOutputStream = new FileOutputStream( directory + fileName );
			} catch ( FileNotFoundException e ) {
				throw new RuntimeException( e );
			}

			try {
				while ( ( length = InputStream.read( buffer ) ) >= 0 ) {
					fileOutputStream.write( buffer, 0, length );
				}
				InputStream.close();
				fileOutputStream.close();
			} catch ( IOException e ) {
				throw new RuntimeException( e );
			}
		}

	}

	private String getFilesDirectory() {
		String directory;
		AppContext appContext = AppContextHolder.getContext();
		directory = Vars.getInstance().getAppsFilesDirectory();
		if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
		directory = directory.concat( appContext.getApplication().getRepositoryID() );

		return directory;
	}

	private void replaceApp( File backupFile ) {
		IRI appIRI = AppContextHolder.getContext().getApplication().getRootContainerIRI();

		InputStream trigInputStream = ZipUtil.unZipFile( backupFile, Vars.getInstance().getAppDataFileName().concat( RDFFormat.TRIG.getDefaultFileExtension() ) );
		if ( trigInputStream == null ) throw new JobException( new Infraction( 0x1012 ) );
		connectionTemplate.write( connection -> connection.remove( (Resource) null, null, null ) );
		connectionTemplate.write( connection -> connection.add( trigInputStream, appIRI.stringValue(), RDFFormat.TRIG ) );
	}

	@Autowired
	public void setNonRDFSourceService( NonRDFSourceService nonRDFSourceService ) { this.nonRDFSourceService = nonRDFSourceService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setConnectionTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) { this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) {
		this.fileRepository = fileRepository;
	}
}
