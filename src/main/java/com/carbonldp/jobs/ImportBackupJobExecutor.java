package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.ldp.nonrdf.RDFRepresentationDescription;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class ImportBackupJobExecutor implements TypedJobExecutor {
	private NonRDFSourceService nonRDFSourceService;
	private RDFSourceService sourceService;
	private SesameConnectionFactory connectionFactory;
	private AppRepository appRepository;
	private TransactionWrapper transactionWrapper;

	@Override
	public boolean supports( JobDescription.Type jobType ) {
		return jobType == JobDescription.Type.IMPORT_BACKUP_JOB;
	}

	@Override
	public void execute( Job job, Execution execution ) {
		if ( ! job.hasType( ImportBackupJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ImportBackupJobDescription.Resource.CLASS.getURI().stringValue() ) );

		URI appURI = job.getAppRelated();
		App app = appRepository.get( appURI );

		ImportBackupJob importBackupJob = new ImportBackupJob( job );
		URI backupURI = importBackupJob.getBackup();
		RDFRepresentation backupRDFRepresentation = new RDFRepresentation( sourceService.get( backupURI ) );

		String mediaType = backupRDFRepresentation.getMediaType();

		if ( ! mediaType.equals( Consts.ZIP ) ) throw new JobException( new Infraction( 0x2005, "property", RDFRepresentationDescription.Property.MEDIA_TYPE.getURI().stringValue() ) );

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
		deleteAppFiles( directory );
		directory.mkdirs();

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile( backupFile );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		ZipEntry directoryZipEntry = null;
		while ( entries.hasMoreElements() ) {
			ZipEntry zipEntry = entries.nextElement();
			if ( zipEntry.isDirectory() ) {
				directoryZipEntry = zipEntry;
				break;
			}
		}
		if ( directoryZipEntry == null ) return;

		InputStream directoryInputStream = null;
		try {
			directoryInputStream = zipFile.getInputStream( directoryZipEntry );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream( directory );
		} catch ( FileNotFoundException e ) {
			throw new RuntimeException( e );
		}

		try {
			while ( ( length = directoryInputStream.read( buffer ) ) >= 0 ) {
				fileOutputStream.write( buffer, 0, length );
			}
			directoryInputStream.close();
			fileOutputStream.close();
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}

	}

	private void deleteAppFiles( File file ) {
		if ( file.isDirectory() ) {
			String files[] = file.list();
			for ( String subFile : files ) {
				File fileDelete = new File( file, subFile );

				deleteAppFiles( fileDelete );
			}
		}
		file.delete();
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
		URI appURI = AppContextHolder.getContext().getApplication().getRootContainerURI();

		InputStream trigInputStream = unZipTrigFile( backupFile );

		try {
			connectionFactory.getConnection().remove( (Resource) null, null, null );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
		try {
			connectionFactory.getConnection().add( trigInputStream, appURI.stringValue(), RDFFormat.TRIG );

		} catch ( IOException e ) {
			throw new RuntimeException( e );
		} catch ( RDFParseException e ) {
			throw new JobException( new Infraction( 0x1015 ) );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	private InputStream unZipTrigFile( File backupFile ) {

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile( backupFile );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		ZipEntry trigZipEntry = null;
		while ( entries.hasMoreElements() ) {
			ZipEntry zipEntry = entries.nextElement();
			String zipEntryName = zipEntry.getName();
			if ( zipEntryName.endsWith( RDFFormat.TRIG.getDefaultFileExtension() ) && ( ! zipEntryName.contains( Consts.SLASH ) && ( ! zipEntryName.contains( Consts.BACK_SLASH ) ) ) ) {
				trigZipEntry = zipEntry;
				break;
			}
		}
		if ( trigZipEntry == null ) {
			throw new JobException( new Infraction( 0x1012 ) );
		}

		InputStream trigInputStream;
		try {
			trigInputStream = zipFile.getInputStream( trigZipEntry );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
		return trigInputStream;
	}

	protected String createRandomSlug() {
		Random random = new Random();
		return String.valueOf( Math.abs( random.nextLong() ) );
	}

	@Autowired
	public void setNonRDFSourceService( NonRDFSourceService nonRDFSourceService ) { this.nonRDFSourceService = nonRDFSourceService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setConnectionFactory( SesameConnectionFactory connectionFactory ) { this.connectionFactory = connectionFactory; }

	@Autowired
	public void setAppRepository( AppRepository appRepository ) { this.appRepository = appRepository; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) { this.transactionWrapper = transactionWrapper; }
}
