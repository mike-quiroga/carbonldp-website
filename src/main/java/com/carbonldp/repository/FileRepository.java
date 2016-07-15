package com.carbonldp.repository;

import com.carbonldp.apps.App;
import org.eclipse.rdf4j.model.IRI;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public interface FileRepository {
	public boolean exists( UUID uuid );

	public File get( UUID uuid );

	public UUID save( File file );

	public void delete( UUID uuid );

	public void deleteDirectory( App app );

	public void emptyDirectory( App app );

	public File createAppRepositoryRDFFile();

	public File createZipFile( Map<File, String> fileToNameMap );

	public IRI createBackupIRI( IRI appIRI );

	public void deleteFile( File file );

	public void deleteDirectory( File file );

	public String getFilesDirectory( App app );
}
