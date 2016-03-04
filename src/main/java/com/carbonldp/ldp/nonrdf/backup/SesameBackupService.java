package com.carbonldp.ldp.nonrdf.backup;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Random;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameBackupService extends AbstractSesameLDPService implements BackupService {
	BackupRepository backupRepository;

	@Override
	public void createAppBackup( URI appURI, URI backupURI, File zipFile ) {
		if ( ! sourceRepository.exists( appURI ) ) throw new ResourceDoesntExistException();
		backupRepository.createAppBackup( appURI, backupURI, zipFile );

	}

	@Autowired
	public void setBackupRepository( BackupRepository backupRepository ) {
		this.backupRepository = backupRepository;
	}
}
