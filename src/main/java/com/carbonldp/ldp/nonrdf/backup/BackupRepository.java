package com.carbonldp.ldp.nonrdf.backup;

import org.eclipse.rdf4j.model.IRI;

import java.io.File;

/**
 * @author NestorVenegas
 * @since 0.33.0
 */
public interface BackupRepository {
	public void createAppBackup( IRI appIRI, IRI backupIRI, File zipFile );
}