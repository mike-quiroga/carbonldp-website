package com.carbonldp.repository.txn;

import com.carbonldp.exceptions.CarbonRuntimeException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class WriteTransactionTemplateImpl extends AbstractTransactionTemplate implements WriteTransactionTemplate {
	private List<WriteTransactionCallback> callbacks;

	public WriteTransactionTemplateImpl( RepositoryConnection connection ) {
		super( connection );
		callbacks = new ArrayList<>();
	}

	@Override
	public void addCallback( WriteTransactionCallback callback ) {
		callbacks.add( callback );
	}

	@Override
	public void execute() {
		beginTransaction();
		try {
			for ( WriteTransactionCallback callback : callbacks ) {
				callback.executeInTransaction( connection );
			}

			commitTransaction();
		} catch ( RepositoryException e ) {
			rollbackTransaction();
			throw new RepositoryRuntimeException( e );
		} catch ( CarbonRuntimeException e ) {
			rollbackTransaction();
			throw e;
		} catch ( Throwable e ) {
			rollbackTransaction();
			throw new CarbonRuntimeException( e );
		} finally {
			closeConnection();
		}
	}

	@Override
	public void execute( WriteTransactionCallback callback ) {
		addCallback( callback );
		execute();
	}

	protected void rollbackTransaction() {
		try {
			connection.rollback();
		} catch ( RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx rollbackTransaction() > The transaction couldn't be rolled back." );
			}
			throw new RepositoryRuntimeException( 0x000B, e );
		}
	}

	protected void commitTransaction() {
		try {
			connection.commit();
		} catch ( RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx commitTransaction() > The transaction couldn't be committed." );
			}
			throw new RepositoryRuntimeException( 0x000C, e );
		}
	}
}
