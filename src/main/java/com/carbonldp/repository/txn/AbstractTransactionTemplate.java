package com.carbonldp.repository.txn;

import com.carbonldp.AbstractComponent;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class AbstractTransactionTemplate extends AbstractComponent {
	protected RepositoryConnection connection;

	public AbstractTransactionTemplate() {
	}

	public AbstractTransactionTemplate( RepositoryConnection connection ) {
		this.connection = connection;
	}

	protected void beginTransaction() {
		try {
			connection.begin();
		} catch ( RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "<< beginTransaction > The transaction couldn't be started." );
			}
			throw new RepositoryRuntimeException( 0x000A, e );
		}
	}

	protected void closeConnection() {
		try {
			connection.close();
		} catch ( RepositoryException e ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "xx closeConnection > Exception Stacktrace:", e );
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "<< closeConnection > The connection couldn't be closed." );
			}
		}
	}
}
