package com.carbonldp.repository;

import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;

public class ConnectionRWTemplate {
	private final SesameConnectionFactory connectionFactory;

	public ConnectionRWTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionFactory = connectionFactory;
	}

	public void write( WriteCallback callback ) {
		RepositoryConnection connection = connectionFactory.getConnection();
		try {
			callback.doWithConnection( connection );
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		} catch ( Exception e ) {
			throw new RepositoryRuntimeException( e );
		}
	}

	public <E> E read( ReadCallback<E> callback ) {
		RepositoryConnection connection = connectionFactory.getConnection();
		try {
			return callback.doWithConnection( connection );
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( e );
		} catch ( MalformedQueryException e ) {
			throw new StupidityException( e );
		} catch ( Exception e ) {
			throw new RepositoryRuntimeException( e );
		}
	}

	public <E> E readStatements( RepositoryResultRetriever<Statement> statementRetriever, RepositoryResultHandler<Statement, E> resultHandler ) {
		return read( connection -> {
			RepositoryResult<Statement> repositoryResult = statementRetriever.retrieveResult( connection );
			E result;
			try {
				result = resultHandler.handle( repositoryResult );
			} finally {
				repositoryResult.close();
			}
			return result;
		} );
	}

	@FunctionalInterface
	public interface RepositoryResultRetriever<E> {
		public RepositoryResult<E> retrieveResult( RepositoryConnection connection ) throws Exception;
	}

	@FunctionalInterface
	public interface RepositoryResultHandler<E, R> {
		public R handle( RepositoryResult<E> repositoryResult ) throws Exception;
	}

	@FunctionalInterface
	public interface WriteCallback {
		public void doWithConnection( RepositoryConnection connection ) throws RepositoryException, MalformedQueryException, Exception;
	}

	@FunctionalInterface
	public interface ReadCallback<E> {
		public E doWithConnection( RepositoryConnection connection ) throws RepositoryException, MalformedQueryException, Exception;
	}
}
