package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import org.openrdf.repository.Repository;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;

import java.io.File;
import java.util.Collection;

/**
 * @author JorgeEspinosa
 * @since 0.27.8-ALPHA
 */
public class UpdateAction1o3o0 extends AbstractUpdateAction {

	final String updateACLTripleQuery =
		"PREFIX cs:	<https://carbonldp.com/ns/v1/security#> \n" +
			"INSERT { ?target cs:accessControlList ?acl } \n" +
			"WHERE { ?acl cs:accessTo ?target }";

	@Override
	public void execute() throws Exception {
		Repository platformRepository = getRepository( Vars.getInstance().getPlatformRepositoryDirectory() );
		executeSPARQLQuery( platformRepository, updateACLTripleQuery );
		closeRepository( platformRepository );

		RepositoryManager repositoryManager = new LocalRepositoryManager( new File( Vars.getInstance().getAppsRepositoryDirectory() ) );
		repositoryManager.initialize();
		Collection<Repository> repositories = repositoryManager.getAllRepositories();
		for ( Repository repository : repositories ) {
			executeSPARQLQuery( repository, updateACLTripleQuery );
		}
		repositoryManager.shutDown();
	}
}
