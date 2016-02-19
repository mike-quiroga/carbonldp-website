package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.authorization.acl.ACLDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.27.8-ALPHA
 */
public class UpdateAction1o3o0 extends AbstractUpdateAction {

	final String updateACLTripleQuery =
		"INSERT { " +
			"GRAPH ?target { " +
			"?target <" + RDFSourceDescription.Property.ACCESS_CONTROL_LIST.getURI().stringValue() + "> ?acl." +
			" }. \n" +
			"} WHERE {" +
			"GRAPH ?acl { ?acl <" + ACLDescription.Property.ACCESS_TO.getURI().stringValue() + "> ?target. " +
			"}." +
			"}";

	@Override
	public void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( updateACLTripleQuery, null ) );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppcontext( app, () -> sparqlTemplate.executeUpdate( updateACLTripleQuery, null ) );
		}
	}
}

