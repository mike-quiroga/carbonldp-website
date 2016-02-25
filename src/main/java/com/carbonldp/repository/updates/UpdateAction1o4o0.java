package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.rdf.RDFBlankNodeDescription;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.*;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.Set;
import java.util.UUID;

/**
 * @author JorgeEspinosa
 * @since _version_
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
			transactionWrapper.runInAppcontext( app, () -> {
				try {
					addBNodeIdentifier();
				} catch ( RepositoryException e ) {
					throw new RuntimeException( e );
				}
			} );
		}
	}

	private void addBNodeIdentifier() throws RepositoryException {

		RepositoryResult<Statement> statements = connectionFactory.getConnection().getStatements( null, null, null, true );
		statements.enableDuplicateFilter();
		while ( statements.hasNext() ) {
			Statement statement = statements.next();
			Resource subject = statement.getSubject();
			if ( ! ( ValueUtil.isBNode( subject ) ) ) continue;
			Resource context = statement.getContext();
			BNode bNode = ValueUtil.getBNode( subject );
			URI predicate = RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getURI();
			if ( connectionFactory.getConnection().hasStatement( bNode, predicate, null, false, context ) ) continue;
			Literal object = ValueFactoryImpl.getInstance().createLiteral( UUID.randomUUID().toString() );
			connectionFactory.getConnection().add( bNode, predicate, object, context );
		}
	}
}
