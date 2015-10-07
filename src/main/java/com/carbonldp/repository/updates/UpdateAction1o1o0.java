package com.carbonldp.repository.updates;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
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
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;

import java.io.File;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class UpdateAction1o1o0 extends AbstractUpdateAction {
	@Override
	public void execute() throws Exception {
		Repository platformRepository = getRepository( Vars.getInstance().getPlatformRepositoryDirectory() );
		changeURIsToHTTPS( platformRepository );
		platformRepository.shutDown();

		RepositoryManager repositoryManager = new LocalRepositoryManager( new File( Vars.getInstance().getAppsRepositoryDirectory() ) );
		repositoryManager.initialize();
		repositoryManager.getAllRepositories().forEach( this::changeURIsToHTTPS );
		repositoryManager.shutDown();
	}

	private void changeURIsToHTTPS( Repository repository ) {
		WriteTransactionTemplate template = new WriteTransactionTemplateImpl( getConnection( repository ) );
		template.execute( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( null, null, null, true );
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

					connection.add( newStatement, context );
					connection.remove( statement, statement.getContext() );
				}
			}
		} );
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
