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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class ApplyBackupJobExecutor implements TypedJobExecutor {
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
		if ( ! job.hasType( ApplyBackupJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ApplyBackupJobDescription.Resource.CLASS.getURI().stringValue() ) );

		URI appURI = job.getAppRelated();
		App app = appRepository.get( appURI );

		ApplyBackupJob applyBackupJob = new ApplyBackupJob( job );
		URI backupURI = applyBackupJob.getBackup();

		RDFRepresentation backupRDFRepresentation = new RDFRepresentation( sourceService.get( backupURI ) );

		String mediaType = backupRDFRepresentation.getMediaType();

		if ( ! mediaType.equals( Consts.ZIP ) ) throw new JobException( new Infraction( 0x2005, "property", RDFRepresentationDescription.Property.MEDIA_TYPE.getURI().stringValue() ) );

		File backupFile = nonRDFSourceService.getResource( backupRDFRepresentation );

		transactionWrapper.runInAppContext( app, () -> replaceApp( backupFile ) );
	}

	private void replaceApp( File backupFile ) {
		URI appURI = AppContextHolder.getContext().getApplication().getRootContainerURI();

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile( backupFile );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		File trigFile = null;
		while ( entries.hasMoreElements() ) {
			ZipEntry zipEntry = entries.nextElement();
			String zipEntryName = zipEntry.getName();
			if ( zipEntryName.endsWith( RDFFormat.TRIG.getDefaultFileExtension() ) ) {
				trigFile = new File( zipEntryName );
				break;
			}
		}
		if ( trigFile == null ) {
			throw new JobException( new Infraction( 0x1012 ) );
		}

		try {
			connectionFactory.getConnection().remove( (Resource) null, null, null );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
		try {
			connectionFactory.getConnection().add( backupFile, appURI.stringValue(), RDFFormat.TRIG );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		} catch ( RDFParseException e ) {
			throw new JobException( new Infraction( 0x1015 ) );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
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
