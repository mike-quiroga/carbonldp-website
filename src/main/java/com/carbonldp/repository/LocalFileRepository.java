package com.carbonldp.repository;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;

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

	public void delete( UUID fileUUID ) {
		File file = get( fileUUID );
		boolean deleted;
		try {
			deleted = file.delete();
		} catch ( SecurityException e ) {
			throw new RuntimeException( "The file couldn't be deleted. Exception:", e );
		}
		if ( ! deleted ) throw new RuntimeException( "The file couldn't be deleted." );
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
			if ( ! created ) throw new RuntimeException( "The parent directory couldn't be created." );
		} else {
			if ( ! directory.isDirectory() ) throw new RuntimeException( "The configured directory is not a directory." );
		}
	}

	private String getFilePath( UUID fileUUID, String directoryPath ) {
		if ( ! directoryPath.endsWith( Consts.SLASH ) ) directoryPath = directoryPath.concat( Consts.SLASH );
		return directoryPath.concat( fileUUID.toString() );
	}

	private String getFilesDirectory() {
		String directory;
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) directory = Vars.getPlatformFilesDirectory();
		else {
			directory = Vars.getAppsFilesDirectory();
			if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
			directory = directory.concat( appContext.getApplication().getRepositoryID() );
		}

		return directory;
	}
}
