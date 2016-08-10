package com.carbonldp.jobs;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
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
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.nquads.NQuadsParser;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.33.0
 */
public class ImportBackupJobExecutor extends AbstractComponent implements TypedJobExecutor {
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

		transactionWrapper.runInAppContext( app, () -> replaceApp( backupFile ) );

		fileRepository.emptyDirectory( app );
		ZipUtil.unZipDirectory( backupFile, Vars.getInstance().getAppDataDirectoryName() + Consts.SLASH, new File( fileRepository.getFilesDirectory( app ) ) );
	}

	private void replaceApp( File backupFile ) {
		IRI appIRI = AppContextHolder.getContext().getApplication().getRootContainerIRI();

		InputStream nQuadsInputStream = ZipUtil.unZipFile( backupFile, Vars.getInstance().getAppDataFileName() + Consts.PERIOD + RDFFormat.NQUADS.getDefaultFileExtension() );

		InputStream configFileStream = ZipUtil.unZipFile( backupFile, Vars.getInstance().getBackupsConfigFile() );

		if ( nQuadsInputStream == null || configFileStream == null ) throw new JobException( new Infraction( 0x1012 ) );

		Map<String, String> configurationMap = fileRepository.getBackupConfiguration( configFileStream );
		Map<String, String> replaceMap = createReplaceMap( configurationMap, AppContextHolder.getContext().getApplication().getIRI().stringValue(), RDFFormat.NQUADS );
		replaceAppNQuadsFile( nQuadsInputStream, appIRI, replaceMap );

	}

	private void replaceAppNQuadsFile( InputStream stream, IRI appIRI, Map<String, String> map ) {
		connectionTemplate.write( connection -> connection.remove( (Resource) null, null, null ) );
		BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
		try {
			addLinesToRepository( reader, appIRI, map );
		} catch ( IOException e ) {
			throw new RuntimeException( "The NQuads file couldn't be read. Exception:", e );
		} finally {
			try {
				reader.close();
			} catch ( IOException e ) {
				LOG.warn( "The buffer couldn't be closed. Exception:", e );
			}
		}
	}

	private void addLinesToRepository( BufferedReader in, IRI appIRI, Map<String, String> map ) throws IOException {
		Set<String> keySet = map.keySet();
		for ( String line = in.readLine(); line != null; line = in.readLine() ) {
			for ( String key : keySet ) {
				line = line.replace( key, map.get( key ) );
			}

			Statement statement = parseLine( line, appIRI );

			if ( statement == null ) continue;

			connectionTemplate.write( connection -> connection.add( statement ) );
		}
	}

	private Statement parseLine( String line, IRI baseIRI ) throws IOException {
		InputStream lineStream = IOUtils.toInputStream( line, "UTF-8" );

		NQuadsParser parser = new NQuadsParser();
		StatementCollector collector = new StatementCollector();

		// BNode IDs need to be preserved because we are parsing line by line
		// If the IDs are not preserved, the parser would create a different BNode ID per statement
		parser.set( BasicParserSettings.PRESERVE_BNODE_IDS, true );
		parser.setRDFHandler( collector );
			parser.parse( lineStream, baseIRI.stringValue() );

		Collection<Statement> statements = collector.getStatements();
		return statements.stream().findFirst().orElse( null );
	}

	private Map<String, String> createReplaceMap( Map<String, String> configurationMap, String app, RDFFormat format ) {
		String protocol = configurationMap.get( Vars.getInstance().getProtocolPlaceHolder() );
		if ( ! protocol.endsWith( "://" ) ) protocol += "://";
		configurationMap.remove( Vars.getInstance().getProtocolPlaceHolder() );
		Map<String, String> replaceMap = new LinkedHashMap<>();
		String appValue = AppContextHolder.getContext().getApplication().getIRI().stringValue();
		appValue = appValue.substring( Vars.getInstance().getAppsContainerURL().length(), appValue.length() - 1 );
		replaceMap.put( protocol + configurationMap.get( Vars.getInstance().getBackupsConfigDomainPlaceholder() ) + "/", Vars.getInstance().getHost() );
		replaceMap.put( configurationMap.get( Vars.getInstance().getBackupsConfigAppPlaceholder() ), appValue );
		return replaceMap;
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
