package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.authorization.acl.ACLDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;

import java.util.Set;

import static com.carbonldp.Consts.*;

/**
 * @author JorgeEspinosa
 * @since 0.27.8-ALPHA
 */
public class UpdateAction1o3o0 extends AbstractUpdateAction {

	final String updateACLTripleQuery = "" +
		"INSERT {" + NEW_LINE +
		TAB + "GRAPH ?target {" + NEW_LINE +
		TAB + TAB + "?target <" + RDFSourceDescription.Property.ACCESS_CONTROL_LIST.getIRI().stringValue() + "> ?acl." + NEW_LINE +
		TAB + "}." + NEW_LINE +
		"} WHERE {" + NEW_LINE +
		TAB + "GRAPH ?acl {" + NEW_LINE +
		TAB + TAB + "?acl <" + ACLDescription.Property.ACCESS_TO.getIRI().stringValue() + "> ?target." + NEW_LINE +
		TAB + "}." + NEW_LINE +
		"}";

	@Override
	public void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( updateACLTripleQuery, null ) );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppContext( app, () -> sparqlTemplate.executeUpdate( updateACLTripleQuery, null ) );
		}
	}
}

