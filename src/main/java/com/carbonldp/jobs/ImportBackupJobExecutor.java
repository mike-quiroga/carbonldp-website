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
 * @since 0.33.0
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

		fileRepository.emptyDirectory( app );
		transactionWrapper.runInAppContext( app, () -> replaceApp( backupFile ) );
		ZipUtil.unZipDirectory( backupFile, Vars.getInstance().getAppDataDirectoryName() + Consts.SLASH, new File( fileRepository.getFilesDirectory( app ) ) );

	}

	private void replaceApp( File backupFile ) {
		IRI appIRI = AppContextHolder.getContext().getApplication().getRootContainerIRI();

		InputStream trigInputStream = ZipUtil.unZipFile( backupFile, Vars.getInstance().getAppDataFileName() + Consts.PERIOD + RDFFormat.TRIG.getDefaultFileExtension() );
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
