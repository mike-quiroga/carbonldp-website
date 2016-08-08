package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.rdf.RDFBlankNodeDescription;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.RepositoryException;

import java.util.Set;
import java.util.UUID;

import static com.carbonldp.Consts.*;

/**
 * add bNodeIdentifier
 *
 * @author JorgeEspinosa
 * @since 0.28.0-ALPHA
 */
public class UpdateAction1o4o0 extends AbstractUpdateAction {

	@Override
	public void execute() throws Exception {

		transactionWrapper.runInPlatformContext( () -> {
			try {
				addBNodeIdentifier();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}
		} );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppContext( app, () -> {
				try {
					addBNodeIdentifier();
				} catch ( RepositoryException e ) {
					throw new RuntimeException( e );
				}
			} );
		}
	}

	private static final String getBNodesWithoutIDQuery;

	static {
		getBNodesWithoutIDQuery = "" +
			"SELECT DISTINCT ?s ?c" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?c" + NEW_LINE +
			TAB + TAB + "{?s ?p ?o}" + NEW_LINE +
			TAB + "FILTER isBlank(?s)." + NEW_LINE +
			TAB + "FILTER NOT EXISTS {?s <" + RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI().stringValue() + "> ?o}" + NEW_LINE +
			"}";
	}

	private void addBNodeIdentifier() throws RepositoryException {
		IRI predicate = RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI();

		sparqlTemplate.executeTupleQuery( getBNodesWithoutIDQuery, null, queryResult -> {
			while ( queryResult.hasNext() ) {
				Literal object = SimpleValueFactory.getInstance().createLiteral( UUID.randomUUID().toString() );
				BindingSet bindingSet = queryResult.next();
				Value subject = bindingSet.getValue( "s" );
				Value context = bindingSet.getValue( "c" );
				connectionFactory.getConnection().add( ValueUtil.getBNode( subject ), predicate, object, ValueUtil.getIRI( context ) );
			}
			return true;
		} );
	}
}