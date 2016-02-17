package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContextExchanger;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authorization.acl.ACLDescription;
import com.carbonldp.namespaces.CS;
import com.carbonldp.sparql.SPARQLTemplate;
import org.openrdf.repository.Repository;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.27.8-ALPHA
 */
public class UpdateAction1o3o0 extends AbstractUpdateAction {

	final String updateACLTripleQuery =
		"INSERT { ?target " + ACLDescription.Resource.CLASS.getURI().stringValue() + " ?acl } \n" +
			"WHERE { ?acl " + ACLDescription.Property.ACCESS_TO.getURI().stringValue() + " ?target }";

	@Override
	public void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> {
			sparqlTemplate.executeUpdate( updateACLTripleQuery, null );
		} );

		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppcontext( app, () -> {
				sparqlTemplate.executeUpdate( updateACLTripleQuery, null );
			} );
		}
	}
}

