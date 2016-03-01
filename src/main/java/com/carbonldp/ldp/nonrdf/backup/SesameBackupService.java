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
	public void createAppBackup( URI appURI, File zipFile ) {
		if ( ! sourceRepository.exists( appURI ) ) throw new ResourceDoesntExistException();
		URI backupURI = createBackupURI( appURI );
		backupRepository.createAppBackup( appURI, backupURI, zipFile );

	}

	private URI createBackupURI( URI appURI ) {
		URI jobsContainerURI = new URIImpl( appURI.stringValue() + Vars.getInstance().getBackupsContainer() );
		URI backupURI;
		do {
			backupURI = new URIImpl( jobsContainerURI.stringValue().concat( createRandomSlug() ).concat( Consts.SLASH ) );
		} while ( sourceRepository.exists( backupURI ) );
		return backupURI;
	}

	private String createRandomSlug() {
		Random random = new Random();
		return String.valueOf( Math.abs( random.nextLong() ) );
	}

	@Autowired
	public void setBackupRepository( BackupRepository backupRepository ) {
		this.backupRepository = backupRepository;
	}
}
