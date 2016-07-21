package com.carbonldp.repository;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.exceptions.FileNotDeletedException;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.NotADirectoryException;
import com.carbonldp.exceptions.NotCreatedException;
import com.carbonldp.models.Infraction;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.utils.NQuadsWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LocalFileRepository implements FileRepository {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private ConnectionRWTemplate connectionTemplate;
	private RDFSourceRepository sourceRepository;

	@Override
	public boolean exists( UUID fileUUID ) {
		File file = get( fileUUID );
		try {
			return file.exists();
		} catch ( SecurityException e ) {
			throw new RuntimeException( "The file couldn't be checked for existence. Exception:", e );
		}
	}

	@Override
	public File get( UUID fileUUID ) {
		String directoryPath = getFilesDirectory();
		String filePath = getFilePath( fileUUID, directoryPath );
		return new File( filePath );
	}

	@Override
	public UUID save( File file ) {
		UUID fileUUID = UUID.randomUUID();

		String directoryPath = getFilesDirectory();
		ensureDirectoryExists( directoryPath );

		String filePath = getFilePath( fileUUID, directoryPath );

		copyFile( file, filePath );

		return fileUUID;
	}

	@Override
	public void delete( UUID fileUUID ) {
		File file = get( fileUUID );
		boolean deleted;
		try {
			deleted = file.delete();
		} catch ( SecurityException e ) {
			throw new RuntimeException( "The file couldn't be deleted. Exception:", e );
		}
		if ( ! deleted ) throw new FileNotDeletedException( 0x1010 );
	}

	@Override
	public void deleteDirectory( App app ) {
		File appDirectory = new File( getFilesDirectory( app ) );
		deleteDirectory( appDirectory );
		if ( appDirectory.exists() ) throw new FileNotDeletedException( 0x1010 );
	}

	@Override
	public void emptyDirectory( App app ) {
		File appDirectory = new File( getFilesDirectory( app ) );
		deleteDirectory( appDirectory );
		if ( appDirectory.exists() ) throw new FileNotDeletedException( 0x1010 );
		appDirectory.mkdir();
	}

	@Override
	public File createAppRepositoryRDFFile( String domainCode, String appCode ) {
		File temporaryFile;
		FileOutputStream outputStream = null;
		final NQuadsWriter nQuadsWriter;
		try {
			temporaryFile = File.createTempFile( Vars.getInstance().getAppDataFileName(), Consts.PERIOD.concat( RDFFormat.NQUADS.getDefaultFileExtension() ) );
			temporaryFile.deleteOnExit();

			outputStream = new FileOutputStream( temporaryFile );
			nQuadsWriter = new NQuadsWriter( outputStream );
			nQuadsWriter.setDomain( Vars.getInstance().getHost() );
			nQuadsWriter.setDomainCode( domainCode );
			String appValue = AppContextHolder.getContext().getApplication().getIRI().stringValue();
			appValue = appValue.substring( Vars.getInstance().getAppsContainerURL().length(), appValue.length() - 1 );
			nQuadsWriter.setApp( appValue );
			nQuadsWriter.setAppCode( appCode );
			connectionTemplate.write( connection -> connection.export( nQuadsWriter ) );

		} catch ( IOException | SecurityException e ) {
			throw new RuntimeException( "The temporary file couldn't be created. Exception:", e );
		} finally {
			try {
				outputStream.close();
			} catch ( IOException e ) {
				LOG.warn( "The outputStream couldn't be closed. Exception: ", e );
			}
		}
		return temporaryFile;
	}

	@Override
	public File createZipFile( Map<File, String> fileToNameMap ) {
		ZipOutputStream zipOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			File temporaryFile;
			try {
				temporaryFile = File.createTempFile( IRIUtil.createRandomSlug(), null );
				temporaryFile.deleteOnExit();
				fileOutputStream = new FileOutputStream( temporaryFile );
			} catch ( FileNotFoundException e ) {
				throw new RuntimeException( "there's no such file", e );
			} catch ( IOException e ) {
				throw new RuntimeException( e );
			}
			zipOutputStream = new ZipOutputStream( fileOutputStream );

			Set<File> files = fileToNameMap.keySet();
			for ( File file : files ) {
				if ( file.isDirectory() ) {
					File[] listFiles = file.listFiles();
					for ( File listFile : listFiles ) {
						addFileToZip( zipOutputStream, listFile, file, fileToNameMap.get( file ) );
					}
				} else {
					addFileToZip( zipOutputStream, file, null, fileToNameMap.get( file ) );
				}
			}
			return temporaryFile;
		} finally {
			try {
				zipOutputStream.close();
				fileOutputStream.close();
			} catch ( IOException e ) {
				LOG.warn( "zip stream could no be closed" );
			}
		}
	}

	@Override
	public IRI createBackupIRI( IRI appIRI ) {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		IRI jobsContainerIRI = valueFactory.createIRI( appIRI.stringValue() + Vars.getInstance().getBackupsContainer() );
		IRI backupIRI;
		do {
			backupIRI = valueFactory.createIRI( jobsContainerIRI.stringValue().concat( IRIUtil.createRandomSlug() ).concat( Consts.SLASH ) );
		} while ( sourceRepository.exists( backupIRI ) );
		return backupIRI;
	}

	@Override
	public void deleteFile( File file ) {
		boolean wasDeleted = false;
		try {
			wasDeleted = file.delete();
		} catch ( SecurityException e ) {
			LOG.warn( "The file couldn't be deleted. Exception:", e );
		}
		if ( ! wasDeleted ) LOG.warn( "The file: '{}', couldn't be deleted.", file.toString() );
	}

	@Override
	public void deleteDirectory( File file ) {
		if ( file.isDirectory() ) {
			String files[] = file.list();
			for ( String subFile : files ) {
				File fileDelete = new File( file, subFile );

				deleteDirectory( fileDelete );
			}
		}
		file.delete();
	}

	@Override
	public File createTempFile( Set<String> tempFileData ) {
		File tempFile = null;
		FileWriter fw = null;
		try {
			tempFile = File.createTempFile( "file:", ".tmp" );
			tempFile.deleteOnExit();
			fw = new FileWriter( tempFile );
			PrintWriter pw = new PrintWriter( fw );
			for ( String line : tempFileData )
				pw.println( line );
		} catch ( Exception e ) {
			throw new RuntimeException( "There is a problem creating the temporary file. Exception: ", e );
		} finally {
			try {
				if ( null != fw )
					fw.close();
			} catch ( Exception e2 ) {
				throw new RuntimeException( "The FileWriter couldn't be closed. Exception: ", e2 );
			}
		}
		return tempFile;
	}

	@Override
	public Map<String, String> getBackupConfiguration( InputStream configStream ) {
		Map<String, String> configuration = new LinkedHashMap<>();
		InputStreamReader inputStreamReader = new InputStreamReader( configStream );
		BufferedReader br = new BufferedReader( inputStreamReader );
		try {
			for ( String line = br.readLine(); line != null; line = br.readLine() ) {
				int div = line.indexOf( "=" );
				if ( div == - 1 ) throw new InvalidResourceException( new Infraction( 0x2015 ) );
				String key = line.substring( 0, div ).trim();
				String value = line.substring( div + 1 ).trim();
				configuration.put( key, value );
			}
			return configuration;
		} catch ( Exception e ) {
			throw new RuntimeException( "There is a problem reading the configuration file. Exception: ", e );
		} finally {
			try {
				if ( null != inputStreamReader )
					inputStreamReader.close();
			} catch ( Exception e2 ) {
				throw new RuntimeException( "The inputStreamReader couldn't be closed. Exception: ", e2 );
			}
		}
	}

	private void addFileToZip( ZipOutputStream zipOutputStream, File file, File directoryFile, String fileNameInsideZip ) {
		FileSystemResource resource = new FileSystemResource( file.getPath() );
		FileInputStream fileInputStream;
		try {
			fileInputStream = (FileInputStream) resource.getInputStream();
		} catch ( IOException e ) {
			throw new RuntimeException( "there's no such file", e );
		}
		ZipEntry zipEntry;
		if ( directoryFile != null ) {
			if ( fileNameInsideZip == null ) fileNameInsideZip = directoryFile.getName();
			zipEntry = new ZipEntry( fileNameInsideZip.concat( Consts.SLASH ).concat( file.getName() ) );
		} else {
			if ( fileNameInsideZip == null ) fileNameInsideZip = file.getName();
			zipEntry = new ZipEntry( fileNameInsideZip );
		}
		try {
			zipOutputStream.putNextEntry( zipEntry );
		} catch ( IOException e ) {
			throw new RuntimeException( "unable to add file ", e );
		}

		byte[] bytes = new byte[1024];
		int length;
		try {
			while ( ( length = fileInputStream.read( bytes ) ) >= 0 ) {
				zipOutputStream.write( bytes, 0, length );
			}
		} catch ( IOException e ) {
			throw new RuntimeException( "unable to add file ", e );
		}
		try {
			zipOutputStream.closeEntry();
			fileInputStream.close();
		} catch ( IOException e ) {
			LOG.warn( "stream could no be closed" );
		}
	}

	private void copyFile( File file, String filePath ) {
		File newFile = new File( filePath );

		try {
			Files.copy( file.toPath(), newFile.toPath() );
		} catch ( IOException | SecurityException e ) {
			throw new RuntimeException( "The file couldn't be copied. Exception:", e );
		}
	}

	private void ensureDirectoryExists( String directoryPath ) {
		File directory = new File( directoryPath );

		boolean exists;
		try {
			exists = directory.exists();
		} catch ( SecurityException e ) {
			throw new RuntimeException( "The directory couldn't be checked for existence. Exception:", e );
		}

		if ( ! exists ) {
			boolean created;
			try {
				created = directory.mkdirs();
			} catch ( SecurityException e ) {
				throw new RuntimeException( "The parent directory couldn't be created. Exception:", e );
			}
			if ( ! created ) throw new NotCreatedException( 0x1013 );
		} else {
			if ( ! directory.isDirectory() ) throw new NotADirectoryException( 0x1014 );
		}
	}

	private String getFilePath( UUID fileUUID, String directoryPath ) {
		if ( ! directoryPath.endsWith( Consts.SLASH ) ) directoryPath = directoryPath.concat( Consts.SLASH );
		return directoryPath.concat( fileUUID.toString() );
	}

	private String getFilesDirectory() {
		String directory;
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) directory = Vars.getInstance().getPlatformFilesDirectory();
		else {
			directory = Vars.getInstance().getAppsFilesDirectory();
			if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
			directory = directory.concat( appContext.getApplication().getRepositoryID() );
		}

		return directory;
	}

	@Override
	public String getFilesDirectory( App app ) {
		String directory = Vars.getInstance().getAppsFilesDirectory();
		if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
		directory = directory.concat( app.getRepositoryID() );

		return directory;
	}

	@Override
	public void removeLineFromFileStartingWith( String file, List<String> linesToRemove ) {

		File inFile = new File( file );
		File tempFile = new File( inFile.getAbsolutePath() + ".tmp" );
		BufferedReader bufferedReader = getBufferedReader( file );
		PrintWriter printWriter = getPrintWriter( tempFile );

		String line = null;
		while ( ( line = readLine( bufferedReader ) ) != null ) {
			if ( ! hasToBeRemoved( line, linesToRemove ) ) {
				printWriter.println( line );
				printWriter.flush();
			}
		}
		printWriter.close();
		try {
			bufferedReader.close();
		} catch ( IOException e ) {
			throw new RuntimeException( "buffered reader could not be closed", e );
		}
		inFile.delete();
		tempFile.renameTo( inFile );

	}

	private String readLine( BufferedReader bufferedReader ) {
		try {
			return bufferedReader.readLine();
		} catch ( IOException e ) {
			throw new RuntimeException( "line can not be readed", e );
		}
	}

	private PrintWriter getPrintWriter( File tempFile ) {
		try {
			return new PrintWriter( new FileWriter( tempFile ) );
		} catch ( IOException e ) {
			throw new RuntimeException( "printWriter could not be created", e );
		}
	}

	private BufferedReader getBufferedReader( String file ) {
		try {
			return new BufferedReader( new FileReader( file ) );
		} catch ( FileNotFoundException e ) {
			throw new RuntimeException( "file could not be read", e );
		}
	}

	private boolean hasToBeRemoved( String line, List<String> linesToRemove ) {
		for ( String lineToRemove : linesToRemove )
			if ( line.trim().startsWith( lineToRemove ) ) return true;
		return false;
	}

	@Autowired
	public void setConnectionTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
