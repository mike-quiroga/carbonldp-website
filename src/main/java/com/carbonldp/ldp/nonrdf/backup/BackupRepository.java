package com.carbonldp.ldp.nonrdf.backup;

import org.openrdf.model.IRI;

import java.io.File;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface BackupRepository {
	public void createAppBackup( IRI appIRI, IRI backupIRI, File zipFile );
}
