package com.carbonldp.test.playground;

import org.openrdf.IsolationLevel;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TransactionalImplications {
	private final TransactionFactory transactionFactory;
	private final ValueFactory valueFactory;

	public TransactionalImplications() {
		Repository repository = getRepository();
		this.transactionFactory = new TransactionFactory( repository );
		this.valueFactory = ValueFactoryImpl.getInstance();
	}

	//@Test
	public void readUsingSPARQL() {
		transactionFactory.enterAndRollback( template -> {
			URI firstResourceURI = new URIImpl( "http://example.org/resources/1" );
			URI secondResourceURI = new URIImpl( "http://example.org/resources/2" );
			URI predicate = new URIImpl( "http://example.org/ns#name" );
			Literal object = valueFactory.createLiteral( "A dummy resource" );
			Value insertedObject;

			// Create the first resource
			template.write( connection -> connection.add( firstResourceURI, predicate, object ) );
			assertTrue( existsUsingSPARQL( firstResourceURI, template ) );

			// Read a property (using SPARQL)
			insertedObject = getPropertyUsingSPARQL( firstResourceURI, predicate, template );
			assertEquals( insertedObject, object );

			// Create the second resource
			template.write( connection -> connection.add( secondResourceURI, predicate, object ) );
			assertTrue( existsUsingSPARQL( secondResourceURI, template ) );

		} );
	}

	//@Test
	public void readInDifferentTransaction() {
		Repository repository = getRepository();
		TransactionFactory transactionFactory = new TransactionFactory( repository );

		URI firstResourceURI = new URIImpl( "http://example.org/resources/1" );
		URI secondResourceURI = new URIImpl( "http://example.org/resources/2" );
		URI predicate = new URIImpl( "http://example.org/ns#name" );
		Literal object = valueFactory.createLiteral( "A dummy resource" );

		transactionFactory.enterAndCommit( template -> {
			// Create the first resource
			template.write( connection -> connection.add( firstResourceURI, predicate, object ) );
			assertTrue( existsUsingSPARQL( firstResourceURI, template ) );

		} );

		transactionFactory.enterAndCommit( template -> {
			Value insertedObject;

			// Read a property (using SPARQL)
			insertedObject = getPropertyUsingSPARQL( firstResourceURI, predicate, template );
			assertEquals( insertedObject, object );

			// Create the second resource
			template.write( connection -> connection.add( secondResourceURI, predicate, object ) );

		} );

		transactionFactory.enterAndRollback( template -> {
			assertTrue( existsUsingSPARQL( secondResourceURI, template ) );
		} );
	}

	//@Test
	public void readUsingSesame() {
		transactionFactory.enterAndRollback( template -> {
			URI firstResourceURI = new URIImpl( "http://example.org/resources/1" );
			URI secondResourceURI = new URIImpl( "http://example.org/resources/2" );
			URI predicate = new URIImpl( "http://example.org/ns#name" );
			Literal object = valueFactory.createLiteral( "A dummy resource" );
			Value insertedObject;

			// Create the first resource
			template.write( connection -> connection.add( firstResourceURI, predicate, object ) );
			assertTrue( existsUsingSPARQL( firstResourceURI, template ) );

			// Read a property
			insertedObject = getProperty( firstResourceURI, predicate, template );
			assertEquals( insertedObject, object );

			// Create the second resource
			template.write( connection -> connection.add( secondResourceURI, predicate, object ) );
			assertTrue( existsUsingSPARQL( secondResourceURI, template ) );
		} );
	}

	private boolean existsUsingSPARQL( URI subject, TransactionTemplate template ) {
		return template.read( connection -> {
			String queryString = "" +
				"ASK {" +
				"	?resource ?predicate ?object." +
				"}";
			BooleanQuery query = connection.prepareBooleanQuery( QueryLanguage.SPARQL, queryString );

			query.setBinding( "resource", subject );

			return query.evaluate();
		} );
	}

	private Value getProperty( URI subject, URI predicate, TransactionTemplate template ) {
		return template.read( connection -> {
			RepositoryResult<Statement> result = connection.getStatements( subject, predicate, null, false );
			if ( ! result.hasNext() ) return null;
			return result.next().getObject();
		} );
	}

	private Value getPropertyUsingSPARQL( URI subject, URI predicate, TransactionTemplate template ) {
		return template.read( connection -> {
			String queryString = "" +
				"SELECT ?object WHERE {" +
				"	?resource ?predicate ?object." +
				"}";
			TupleQuery query = connection.prepareTupleQuery( QueryLanguage.SPARQL, queryString );
			query.setBinding( "resource", subject );
			query.setBinding( "predicate", predicate );
			TupleQueryResult result = query.evaluate();
			if ( ! result.hasNext() ) return null;
			Value valueResult = result.next().getBinding( "object" ).getValue();
			result.close();
			return valueResult;
		} );
	}

	private Repository getRepository() {
		Repository repository = new SailRepository( new MemoryStore() );
		try {
			repository.initialize();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
		return repository;
	}

	public class TransactionFactory {
		private final Repository repository;

		public TransactionFactory( Repository repository ) {
			this.repository = repository;
		}

		public TransactionTemplate get() {
			return new TransactionTemplate( getConnection() );
		}

		public TransactionTemplate get( IsolationLevel isolationLevel ) {
			return new TransactionTemplate( getConnection(), isolationLevel );
		}

		public void enterAndCommit( TransactionalAction action ) {
			TransactionTemplate template = get();
			enterAndCommit( action, template );
		}

		public void enterAndCommit( IsolationLevel isolationLevel, TransactionalAction action ) {
			TransactionTemplate template = get( isolationLevel );
			enterAndCommit( action, template );
		}

		public void enterAndRollback( TransactionalAction action ) {
			TransactionTemplate template = get();
			enterAndRollback( action, template );
		}

		public void enterAndRollback( IsolationLevel isolationLevel, TransactionalAction action ) {
			TransactionTemplate template = get( isolationLevel );
			enterAndRollback( action, template );
		}

		private void enterAndCommit( TransactionalAction action, TransactionTemplate template ) {
			try {
				action.execute( template );
				template.commit();
			} catch ( Throwable e ) {
				template.rollback();
				if ( e instanceof RuntimeException ) throw (RuntimeException) e;
				else throw new RuntimeException( e );
			}
		}

		private void enterAndRollback( TransactionalAction action, TransactionTemplate template ) {
			try {
				action.execute( template );
				template.rollback();
			} catch ( Throwable e ) {
				template.rollback();
				if ( e instanceof RuntimeException ) throw (RuntimeException) e;
				else throw new RuntimeException( e );
			}
		}

		private RepositoryConnection getConnection() {
			try {
				return repository.getConnection();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}
		}
	}

	public class TransactionTemplate {
		private final RepositoryConnection connection;
		private IsolationLevel isolationLevel;

		public TransactionTemplate( RepositoryConnection connection ) {
			this.connection = connection;
		}

		public TransactionTemplate( RepositoryConnection connection, IsolationLevel isolationLevel ) {
			this( connection );
			this.isolationLevel = isolationLevel;
		}

		public <E> E read( Read<E> readAction ) {
			RepositoryConnection connection = getConnection();
			try {
				return readAction.execute( connection );
			} catch ( Exception e ) {
				rollback();
				throw new RuntimeException( e );
			}
		}

		public void write( Write writeAction ) {
			RepositoryConnection connection = getConnection();
			try {
				writeAction.execute( connection );
			} catch ( Exception e ) {
				rollback();
				throw new RuntimeException( e );
			}
		}

		private RepositoryConnection getConnection() {
			try {
				if ( ! connection.isOpen() ) throw new RuntimeException( "Connection has already been closed." );
				if ( ! connection.isActive() ) init();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}

			return connection;
		}

		private void init() throws RepositoryException {
			if ( isolationLevel == null ) connection.begin();
			else connection.begin( isolationLevel );
		}

		public void commit() {
			try {
				connection.commit();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			} finally {
				close();
			}
		}

		public void rollback() {
			try {
				connection.rollback();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			} finally {
				close();
			}
		}

		private void close() {
			try {
				connection.close();
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}
		}
	}

	//@FunctionalInterface
	public interface TransactionalAction {
		public void execute( TransactionTemplate template ) throws Exception;
	}

	//@FunctionalInterface
	public interface Read<R> {
		public R execute( RepositoryConnection repositoryConnection ) throws Exception;
	}

	//@FunctionalInterface
	public interface Write {
		public void execute( RepositoryConnection repositoryConnection ) throws Exception;
	}
}
