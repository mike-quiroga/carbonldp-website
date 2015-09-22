package org.openrdf.sail.nativerdf;

import info.aduna.iteration.CloseableIteration;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.io.IOException;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class CarbonStore extends NativeStore {

	@Override
	protected CloseableIteration<? extends Statement, IOException> createStatementIterator( Resource subj, URI pred, Value obj, boolean includeInferred, boolean readTransaction, Resource... contexts ) throws IOException {
		CloseableIteration<? extends Statement, IOException> originalIteration = super.createStatementIterator( subj, pred, obj, includeInferred, readTransaction, contexts );

		return new SecuredRepositoryResult( originalIteration, statement -> {
			return true;
			/*
				if( ! securityIsEnabled() ) return statement;

				disableSecurity();

				boolean allowed = true;
				for( securityAllower : securityAllowers ) {
					if( ! securityAllower.canAccess( statement ) ) {
						allowed = false;
						break;
					}
				}

				enableSecurity();

				return allowed;
			 */
		} );
	}

	public class SecuredRepositoryResult implements CloseableIteration<Statement, IOException> {

		private SecurityAccessApprover filterer;
		private CloseableIteration<? extends Statement, IOException> originalIteration;
		private Statement nextStatement;

		public SecuredRepositoryResult( CloseableIteration<? extends Statement, IOException> originalIteration, SecurityAccessApprover filterer ) {
			this.originalIteration = originalIteration;
			this.filterer = filterer;
		}

		@Override
		public void close() throws IOException {
			this.originalIteration.close();
		}

		@Override
		public boolean hasNext() throws IOException {
			if ( this.nextStatement != null ) return true;
			while ( this.originalIteration.hasNext() ) {
				Statement statement = this.originalIteration.next();
				if ( ! this.filterer.canAccess( statement ) ) continue;

				this.nextStatement = statement;
				return true;
			}
			return false;
		}

		@Override
		public Statement next() throws IOException {
			if ( ! this.hasNext() ) return null;

			Statement statement = this.nextStatement;
			this.nextStatement = null;
			return statement;
		}

		@Override
		public void remove() throws IOException {
			throw new IOException();
		}
	}

	@FunctionalInterface
	public interface SecurityAccessApprover {
		public boolean canAccess( Statement statement );
	}
}
