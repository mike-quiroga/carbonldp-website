package com.carbonldp.ldp.nonrdf.backup;

import org.openrdf.model.IRI;

import java.io.File;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public interface BackupService {

	public void createAppBackup( IRI appIRI, IRI backupIRI, File zipFile );
}
