package com.carbonldp.jobs;

import com.carbonldp.Consts;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
		return jobType == JobDescription.Type.APPLY_BACKUP;
	}

	@Override
	@Transactional
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

		transactionWrapper.runInAppContext( app, () -> replaceApp( backupFile ) );
	}

	private void replaceApp( File backupFile ) {
		URI appURI = AppContextHolder.getContext().getApplication().getRootContainerURI();

		File trigFile = unZipTrigFile( backupFile );

		try {
			connectionFactory.getConnection().remove( (Resource) null, null, null );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
		try {
			connectionFactory.getConnection().add( trigFile, appURI.stringValue(), RDFFormat.TRIG );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		} catch ( RDFParseException e ) {
			throw new JobException( new Infraction( 0x1015 ) );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	private File unZipTrigFile( File backupFile ) {
		byte[] buffer = new byte[1024];
		int length;
		File temporaryFile;

		try {
			temporaryFile = File.createTempFile( createRandomSlug(), null );
			temporaryFile.deleteOnExit();
		} catch ( IOException | SecurityException e ) {
			throw new RuntimeException( "The temporary file couldn't be created. Exception:", e );
		}

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
			if ( zipEntryName.endsWith( RDFFormat.TRIG.getDefaultFileExtension() ) ) {
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

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream( temporaryFile );
		} catch ( FileNotFoundException e ) {
			throw new RuntimeException( e );
		}

		try {
			while ( ( length = trigInputStream.read( buffer ) ) >= 0 ) {
				fileOutputStream.write( buffer, 0, length );
			}
			trigInputStream.close();
			fileOutputStream.close();
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}

		return temporaryFile;
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
