package org.openrdf.sail.nativerdf;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.ExceptionConvertingIteration;
import org.openrdf.model.*;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.algebra.evaluation.TripleSource;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class CarbonTripleSource implements TripleSource {

	/*-----------*
	 * Constants *
	 *-----------*/

	protected final NativeStore nativeStore;

	protected final boolean includeInferred;

	protected final boolean readTransaction;

	/*--------------*
	 * Constructors *
	 *--------------*/

	protected CarbonTripleSource( NativeStore store, boolean includeInferred, boolean readTransaction ) {
		this.nativeStore = store;
		this.includeInferred = includeInferred;
		this.readTransaction = readTransaction;
	}

	/*---------*
	 * Methods *
	 *---------*/

	public CloseableIteration<? extends Statement, QueryEvaluationException> getStatements( Resource subj,
		URI pred, Value obj, Resource... contexts )
		throws QueryEvaluationException {
		try {
			return new ExceptionConvertingIteration<Statement, QueryEvaluationException>( nativeStore.createStatementIterator( subj, pred, obj, includeInferred, readTransaction, contexts ) ) {

				@Override
				protected QueryEvaluationException convert( Exception e ) {
					if ( e instanceof ClosedByInterruptException ) {
						return new QueryInterruptedException( e );
					} else if ( e instanceof IOException ) {
						return new QueryEvaluationException( e );
					} else if ( e instanceof RuntimeException ) {
						throw (RuntimeException) e;
					} else if ( e == null ) {
						throw new IllegalArgumentException( "e must not be null" );
					} else {
						throw new IllegalArgumentException( "Unexpected exception type: " + e.getClass() );
					}
				}

			};
		} catch ( IOException e ) {
			throw new QueryEvaluationException( "Unable to get statements", e );
		}
	}

	public ValueFactory getValueFactory() {
		return nativeStore.getValueFactory();
	}
}
