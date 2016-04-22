package com.carbonldp.repository;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.exceptions.FileNotDeletedException;
import com.carbonldp.exceptions.NotADirectoryException;
import com.carbonldp.exceptions.NotCreatedException;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.utils.TriGWriter;
import org.apache.commons.io.FileUtils;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.spring.SesameConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Random;
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
	public File createAppRepositoryRDFFile() {
		File temporaryFile;
		FileOutputStream outputStream = null;
		final TriGWriter trigWriter;
		try {
			temporaryFile = File.createTempFile( Vars.getInstance().getAppDataFileName(), Consts.PERIOD.concat( RDFFormat.TRIG.getDefaultFileExtension() ) );
			temporaryFile.deleteOnExit();

			outputStream = new FileOutputStream( temporaryFile );
			trigWriter = new TriGWriter( outputStream );
			trigWriter.setBase( AppContextHolder.getContext().getApplication().getRootContainerIRI().stringValue() );
			connectionTemplate.write( connection -> connection.export( trigWriter ) );

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
	public File createZipFile( Map<File, String> entries ) {
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

			Set<File> files = entries.keySet();
			for ( File file : files ) {
				if ( file.isDirectory() ) {
					File[] listFiles = file.listFiles();
					for ( File listFile : listFiles ) {
						addFileToZip( zipOutputStream, listFile, file, entries.get( file ) );
					}
				} else {
					addFileToZip( zipOutputStream, file, null, entries.get( file ) );
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
			zipEntry = new ZipEntry( fileNameInsideZip.concat( Consts.SLASH ).concat( file.getName() ) );
		} else {
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

	@Autowired
	public void setConnectionTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
