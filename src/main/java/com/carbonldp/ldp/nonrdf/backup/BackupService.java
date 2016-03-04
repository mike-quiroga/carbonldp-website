package com.carbonldp.ldp.nonrdf.backup;

import org.openrdf.model.URI;

import java.io.File;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public interface BackupService {

	public void createAppBackup( URI appURI, URI backupURI, File zipFile );
}
