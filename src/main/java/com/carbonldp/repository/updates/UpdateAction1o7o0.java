package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.rdf.RDFBlankNodeDescription;
import org.openrdf.model.vocabulary.RDF;

import java.util.Set;

import static com.carbonldp.Consts.*;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o7o0 extends AbstractUpdateAction {
	final String updateBNodeListQuery = "" +
		"DELETE {" + NEW_LINE +
		TAB + "?target <" + RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI().stringValue() + "> ?bNodeID." + NEW_LINE +
		"} WHERE {" + NEW_LINE +
		TAB + "?target <" + RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI().stringValue() + "> ?bNodeID." + NEW_LINE +
		TAB + "?target <" + RDF.FIRST.stringValue() + "> ?first." + NEW_LINE +
		TAB + "?target <" + RDF.REST.stringValue() + "> ?rest." + NEW_LINE +
		"FILTER isBlank(?target)" + NEW_LINE +
		"}";

	@Override
	protected void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( updateBNodeListQuery, null ) );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppContext( app, () -> sparqlTemplate.executeUpdate( updateBNodeListQuery, null ) );
		}
	}
}
