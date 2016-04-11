package com.carbonldp.repository.updates;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.namespaces.C;
import com.carbonldp.namespaces.CP;
import com.carbonldp.namespaces.CS;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.Set;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class UpdateAction1o1o0 extends AbstractUpdateAction {

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

		RepositoryResult<Statement> statements = connectionFactory.getConnection().getStatements( null, null, null, true );

		while ( statements.hasNext() ) {
			Statement statement = statements.next();
			Resource context = statement.getContext();
			Resource subject = statement.getSubject();
			URI predicate = statement.getPredicate();
			Value object = statement.getObject();

			if ( ValueUtil.isIRI( context ) && uriNeedsToBeChanged( ValueUtil.getIRI( context ) ) ) context = changeProtocol( ValueUtil.getIRI( context ) );
			if ( ValueUtil.isIRI( subject ) && uriNeedsToBeChanged( ValueUtil.getIRI( subject ) ) ) subject = changeProtocol( ValueUtil.getIRI( subject ) );
			if ( uriNeedsToBeChanged( predicate ) ) predicate = changeProtocol( predicate );
			if ( ValueUtil.isIRI( object ) && uriNeedsToBeChanged( ValueUtil.getIRI( object ) ) ) object = changeProtocol( ValueUtil.getIRI( object ) );

			if ( context != statement.getContext() || subject != statement.getSubject() || predicate != statement.getPredicate() || object != statement.getObject() ) {
				Statement newStatement = new ContextStatementImpl( subject, predicate, object, context );

				if ( LOG.isDebugEnabled() ) LOG.debug( "changeURIsToHTTPS() -- Changing statement: '{}' to '{}'", statement, newStatement );

				connectionFactory.getConnection().add( newStatement, context );
				connectionFactory.getConnection().remove( statement, statement.getContext() );

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
