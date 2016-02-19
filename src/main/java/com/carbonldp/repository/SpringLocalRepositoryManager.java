package com.carbonldp.repository;

import org.openrdf.repository.manager.LocalRepositoryManager;

import javax.annotation.PreDestroy;
import java.io.File;

/**
 * @author MiguelAraCo
 * @since 0.27.6-ALPHA
 */
public class SpringLocalRepositoryManager extends LocalRepositoryManager {
	/**
	 * Creates a new RepositoryManager that operates on the specfified base
	 * directory.
	 *
	 * @param baseDir
	 * 	The base directory where data for repositories can be stored, among
	 * 	other things.
	 */
	public SpringLocalRepositoryManager( File baseDir ) {
		super( baseDir );
	}

	@PreDestroy
	public void destroy() {
		super.shutDown();
	}
}
