package com.carbonldp.repository.updates;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.namespaces.C;
import com.carbonldp.namespaces.CP;
import com.carbonldp.namespaces.CS;
import com.carbonldp.repository.txn.WriteTransactionTemplate;
import com.carbonldp.repository.txn.WriteTransactionTemplateImpl;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class UpdateAction1o1o0 extends AbstractUpdateAction {
	private final String getAllQuery = "" +
		"CONSTRUCT{\n" +
		"   GRAPH ?c \n" +
		"       {?s ?p ?o}.\n" +
		"   }WHERE{\n" +
		"    GRAPH ?c\n" +
		"        {?s ?p ?o}.\n" +
		"    }";

	private final String deleteTripleQuery = "" +
		"DELETE{" +
		"   GRAPH ?c" +
		"       {?s ?p ?o}" +
		"}WHERE{\n" +
		"    GRAPH ?c\n" +
		"        {?s ?p ?o}\n" +
		"    }";

	private final String addTripleQuery = "" +
		"INSERT{" +
		"   GRAPH ?c" +
		"       {?s ?p ?o}" +
		"}WHERE{\n" +
		"    GRAPH ?c\n" +
		"        {?s ?p ?o}\n" +
		"    }";

	@Override
	public void execute() throws Exception {

		transactionWrapper.runInPlatformContext( () -> {
			try {
				changeURIsToHTTPS();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}
		} );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInAppcontext( app, () -> {
				try {
					changeURIsToHTTPS();
				} catch ( RepositoryException e ) {
					throw new RuntimeException( e );
				}
			} );
		}

	}

	private void changeURIsToHTTPS() throws RepositoryException {

		RepositoryResult<Statement> statements = sparqlTemplate.executeGraphQuery( getAllQuery, null );

		while ( statements.hasNext() ) {
			Statement statement = statements.next();
			Resource context = statement.getContext();
			Resource subject = statement.getSubject();
			URI predicate = statement.getPredicate();
			Value object = statement.getObject();

			if ( ValueUtil.isURI( context ) && uriNeedsToBeChanged( ValueUtil.getURI( context ) ) ) context = changeProtocol( ValueUtil.getURI( context ) );
			if ( ValueUtil.isURI( subject ) && uriNeedsToBeChanged( ValueUtil.getURI( subject ) ) ) subject = changeProtocol( ValueUtil.getURI( subject ) );
			if ( uriNeedsToBeChanged( predicate ) ) predicate = changeProtocol( predicate );
			if ( ValueUtil.isURI( object ) && uriNeedsToBeChanged( ValueUtil.getURI( object ) ) ) object = changeProtocol( ValueUtil.getURI( object ) );

			if ( context != statement.getContext() || subject != statement.getSubject() || predicate != statement.getPredicate() || object != statement.getObject() ) {
				Statement newStatement = new ContextStatementImpl( subject, predicate, object, context );

				if ( LOG.isDebugEnabled() ) LOG.debug( "changeURIsToHTTPS() -- Changing statement: '{}' to '{}'", statement, newStatement );

				Map<String, Value> addValues = new HashMap<>();
				addValues.put( "c", newStatement.getContext() );
				addValues.put( "s", newStatement.getSubject() );
				addValues.put( "p", newStatement.getPredicate() );
				addValues.put( "o", newStatement.getObject() );

				Map<String, Value> removeValues = new HashMap<>();
				removeValues.put( "c", statement.getContext() );
				removeValues.put( "s", statement.getSubject() );
				removeValues.put( "p", statement.getPredicate() );
				removeValues.put( "o", statement.getObject() );

				sparqlTemplate.executeUpdate( deleteTripleQuery, removeValues );
				sparqlTemplate.executeUpdate( addTripleQuery, addValues );

			}
		}
	}

	private boolean uriNeedsToBeChanged( URI uri ) {
		String host = Consts.HTTP + Vars.getInstance().getHost().substring( Consts.HTTPS.length() );
		String c = Consts.HTTP + C.NAMESPACE.substring( Consts.HTTPS.length() );
		String cp = Consts.HTTP + CP.NAMESPACE.substring( Consts.HTTPS.length() );
		String cs = Consts.HTTP + CS.NAMESPACE.substring( Consts.HTTPS.length() );
		return uri.stringValue().startsWith( host ) || uri.stringValue().startsWith( c ) || uri.stringValue().startsWith( cp ) || uri.stringValue().startsWith( cs );
	}

	private URI changeProtocol( URI uri ) {
		return new URIImpl( Consts.HTTPS + uri.stringValue().substring( Consts.HTTP.length() ) );
	}
}
