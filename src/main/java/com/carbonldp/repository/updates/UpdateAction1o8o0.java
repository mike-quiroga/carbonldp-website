package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.namespaces.LDP;

import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o8o0 extends AbstractUpdateAction {
	final String updateIsMemberOfRelationQuery = "" +
		"DELETE { " + NEW_LINE +
		TAB + "?s <http://www.w3.org/ns/ldp#memberOfRelation> ?o " + NEW_LINE +
		"}" + NEW_LINE +
		"INSERT { " + NEW_LINE +
		TAB + "?s <" + LDP.Properties.IS_MEMBER_OF_RELATION + "> ?o " + NEW_LINE +
		"}" + NEW_LINE +
		"WHERE { " + NEW_LINE +
		TAB + "?s ldp:memberOfRelation ?o" + NEW_LINE +
		"}";

	@Override
	protected void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( updateIsMemberOfRelationQuery, null ) );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppContext( app, () -> sparqlTemplate.executeUpdate( updateIsMemberOfRelationQuery, null ) );
		}
	}
}