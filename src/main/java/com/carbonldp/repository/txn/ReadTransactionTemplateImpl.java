package com.carbonldp.repository.txn;

import com.carbonldp.exceptions.CarbonRuntimeException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class ReadTransactionTemplateImpl<T> extends AbstractTransactionTemplate implements ReadTransactionTemplate<T> {
	public ReadTransactionTemplateImpl( RepositoryConnection connection ) {
		super();
		RepositoryConnection readOnlyConnection = new ReadOnlyRepositoryConnection( connection );
		this.connection = readOnlyConnection;
	}

	@Override
	public T execute( ReadTransactionCallback<T> callback ) {
		try {
			return callback.executeInTransaction( connection );
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( e );
		} catch ( CarbonRuntimeException e ) {
			throw e;
		} catch ( Throwable e ) {
			throw new CarbonRuntimeException( e );
		} finally {
			closeConnection();
		}
	}
}