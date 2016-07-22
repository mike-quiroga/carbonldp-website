package com.carbonldp.playground;

import com.carbonldp.AbstractComponent;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.algebra.evaluation.EvaluationStrategy;
import org.eclipse.rdf4j.query.algebra.evaluation.TripleSource;
import org.eclipse.rdf4j.query.algebra.evaluation.federation.FederatedServiceResolverImpl;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.SimpleEvaluationStrategy;
import org.eclipse.rdf4j.query.impl.EmptyBindingSet;
import org.eclipse.rdf4j.query.parser.ParsedTupleQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
public class OnMemoryRepository extends AbstractComponent {
	public static void main( String[] args ) {
		OnMemoryRepository test = new OnMemoryRepository();
		try {
			test.run();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	public OnMemoryRepository() {

	}

	public void run() throws Exception {
		String modelString = "" +
			"<http://example.org/resource-1> <http://example.org/ns#property> <http://example.org/resource-1>." +
			"<http://example.org/resource-2> <http://example.org/ns#property> <http://example.org/resource-2>." +
			"<http://example.org/resource-3> <http://example.org/ns#property> <http://example.org/resource-3>.";

		AbstractModel model = readModel( modelString, RDFFormat.TRIG );

		String query = "" +
			"SELECT" +
			"	?s ?p ?o " +
			"WHERE { " +
			"	?s ?p ?o " +
			"}";

		Repository repository = new SailRepository( new MemoryStore() );
		repository.initialize();
		RepositoryConnection connection = repository.getConnection();
		connection.add( model );

		TripleSource tripleSource = new TripleSourceImpl( connection, statement -> {
			return ! statement.getSubject().equals( SimpleValueFactory.getInstance().createIRI( "http://example.org/resource-2" ) );
		} );

		ParsedTupleQuery parsedTupleQuery = QueryParserUtil.parseTupleQuery( QueryLanguage.SPARQL, query, null );
		EvaluationStrategy evaluationStrategy = new SimpleEvaluationStrategy( tripleSource, new FederatedServiceResolverImpl() );
		CloseableIteration<BindingSet, QueryEvaluationException> bindingSetIterator = evaluationStrategy.evaluate( parsedTupleQuery.getTupleExpr(), new EmptyBindingSet() );

		while ( bindingSetIterator.hasNext() ) {
			BindingSet bindingSet = bindingSetIterator.next();

			for ( Binding binding : bindingSet ) {
				LOG.debug( binding.getName() + ": " + binding.getValue().stringValue() );
			}
		}
	}

	private AbstractModel readModel( String modelString, RDFFormat format ) throws Exception {

		InputStream inputStream = new ByteArrayInputStream( modelString.getBytes( StandardCharsets.UTF_8 ) );

		RDFParser parser = Rio.createParser( format );
		AbstractModel model = new LinkedHashModel();

		parser.setRDFHandler( new StatementCollector( model ) );
		parser.parse( inputStream, "" );

		return model;
	}

	public class TripleSourceImpl implements TripleSource {
		private RepositoryConnection connection;
		private SecurityFilterer securityFilter;

		public TripleSourceImpl( RepositoryConnection connection, SecurityFilterer securityFilterer ) {
			this.connection = connection;
			this.securityFilter = securityFilterer;
		}

		@Override
		public CloseableIteration<? extends Statement, QueryEvaluationException> getStatements( Resource subj, IRI pred, Value obj, Resource... contexts ) throws QueryEvaluationException {
			// We could filter contexts from here, so they wouldnt get pulled from the repository
			try {
				return new SecuredRepositoryResult( this.connection.getStatements( subj, pred, obj, false, contexts ), this.securityFilter );
			} catch ( RepositoryException e ) {
				throw new QueryEvaluationException( e );
			}
		}

		@Override
		public ValueFactory getValueFactory() {
			return this.connection.getValueFactory();
		}
	}

	public class SecuredRepositoryResult implements CloseableIteration<Statement, QueryEvaluationException> {

		private SecurityFilterer filterer;
		private RepositoryResult<Statement> repositoryResult;
		private Statement nextStatement;

		public SecuredRepositoryResult( RepositoryResult<Statement> repositoryResult, SecurityFilterer filterer ) {
			this.repositoryResult = repositoryResult;
			this.filterer = filterer;
		}

		@Override
		public void close() throws QueryEvaluationException {
			try {
				this.repositoryResult.close();
			} catch ( RepositoryException e ) {
				throw new QueryEvaluationException( e );
			}
		}

		@Override
		public boolean hasNext() throws QueryEvaluationException {
			if ( this.nextStatement != null ) return true;
			try {

				while ( this.repositoryResult.hasNext() ) {
					Statement statement = this.repositoryResult.next();
					if ( ! this.filterer.canAccess( statement ) ) continue;

					this.nextStatement = statement;
					return true;
				}
				return false;

			} catch ( RepositoryException e ) {
				throw new QueryEvaluationException( e );
			}
		}

		@Override
		public Statement next() throws QueryEvaluationException {
			if ( ! this.hasNext() ) return null;

			Statement statement = this.nextStatement;
			this.nextStatement = null;
			return statement;
		}

		@Override
		public void remove() throws QueryEvaluationException {
			throw new QueryEvaluationException();
		}
	}

	@FunctionalInterface
	public interface SecurityFilterer {
		public boolean canAccess( Statement statement );
	}
}
