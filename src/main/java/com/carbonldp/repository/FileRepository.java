package com.carbonldp.repository;

import com.carbonldp.apps.App;
import org.openrdf.model.IRI;

import java.io.File;
import java.util.UUID;

public interface FileRepository {
	public boolean exists( UUID uuid );

	public File get( UUID uuid );

	public UUID save( File file );

	public void delete( UUID uuid );

	public void deleteDirectory( App app );

	public File createAppRepositoryRDFFile();

	public File createZipFile( File... files );

	public IRI createBackupIRI( IRI appIRI );
}
