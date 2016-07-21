package com.carbonldp.repository;

import com.carbonldp.apps.App;
import org.eclipse.rdf4j.model.IRI;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface FileRepository {
	public boolean exists( UUID uuid );

	public File get( UUID uuid );

	public UUID save( File file );

	public void delete( UUID uuid );

	public void deleteDirectory( App app );

	public void emptyDirectory( App app );

	public File createAppRepositoryRDFFile( String domainCode, String appCode );

	public File createZipFile( Map<File, String> fileToNameMap );

	public IRI createBackupIRI( IRI appIRI );

	public void deleteFile( File file );

	public void deleteDirectory( File file );

	public File createTempFile( Set<String> tempFileData );

	public Map<String, String> getBackupConfiguration( InputStream configStream );

	public String getFilesDirectory( App app );

	public void removeLineFromFileStartingWith( String file, List<String> linesToRemove );
}
