package com.carbonldp.ldp.nonrdf.backup;

import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameBackupService extends AbstractSesameLDPService implements BackupService {
	BackupRepository backupRepository;

	@Override
	public void createAppBackup( IRI appIRI, IRI backupIRI, File zipFile ) {
		if ( ! sourceRepository.exists( appIRI ) ) throw new ResourceDoesntExistException();
		backupRepository.createAppBackup( appIRI, backupIRI, zipFile );

	}

	@Autowired
	public void setBackupRepository( BackupRepository backupRepository ) {
		this.backupRepository = backupRepository;
	}
}
