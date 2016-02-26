package com.carbonldp.ldp.nonrdf.backup;

import org.openrdf.model.URI;

import java.io.File;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface BackupRepository {
	public void createAppBackup( URI appURI, URI backupURI, File zipFile );
}
