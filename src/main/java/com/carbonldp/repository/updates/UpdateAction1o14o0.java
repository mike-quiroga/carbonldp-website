package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.authorization.acl.ACLDescription;
import com.carbonldp.ldp.Documents.ProtectedDocumentDescription;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Set;

import static com.carbonldp.Consts.*;

/**
 * add protectedDocument type to all resources but acl
 *
 * @author NestorVenegas
 * @since 0.40.0
 */
public class UpdateAction1o14o0 extends AbstractUpdateAction {

	final String addProtectedDocumentType = "" +
		"INSERT { " + NEW_LINE +
		TAB + "GRAPH ?g {" + NEW_LINE +
		TAB + TAB + "?g <" + RDF.TYPE + "> <" + ProtectedDocumentDescription.Resource.CLASS.getIRI() + ">" + NEW_LINE +
		TAB + "}" + NEW_LINE +
		"}" + NEW_LINE +
		"WHERE { " + NEW_LINE +
		TAB + "GRAPH ?g {" + NEW_LINE +
		TAB + TAB + "?g <" + RDF.TYPE + "> ?types" + NEW_LINE +
		TAB + "}" + NEW_LINE +
		TAB + "MINUS {" + NEW_LINE +
		TAB + TAB + "GRAPH ?g {" + NEW_LINE +
		TAB + TAB + TAB + "?g <" + RDF.TYPE + "> <" + ACLDescription.Resource.CLASS.getIRI() + ">" + NEW_LINE +
		TAB + TAB + "}" + NEW_LINE +
		TAB + "}" + NEW_LINE +
		"}";

	@Override
	protected void execute() throws Exception {
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( addProtectedDocumentType, null ) );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppContext( app, () -> sparqlTemplate.executeUpdate( addProtectedDocumentType, null ) );
		}
	}
}
