package com.carbonldp.repository;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.exceptions.FileNotDeletedException;
import com.carbonldp.exceptions.NotADirectoryException;
import com.carbonldp.exceptions.NotCreatedException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class LocalFileRepository implements FileRepository {

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
		try {
			FileUtils.deleteDirectory( appDirectory );
		} catch ( IOException e ) {
			throw new RuntimeException( "The file couldn't be deleted. Exception:", e );
		}
		if ( appDirectory.exists() ) throw new FileNotDeletedException( 0x1010 );

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

	private String getFilesDirectory( App app ) {
		String directory = Vars.getInstance().getAppsFilesDirectory();
		if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
		directory = directory.concat( app.getRepositoryID() );

		return directory;
	}
}
