package com.carbonldp.repository.security;

import info.aduna.iteration.CloseableIteration;
import org.openrdf.IsolationLevels;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.sail.nativerdf.NativeStore;

import java.io.File;
import java.io.IOException;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class SecuredNativeStore extends NativeStore {

	public SecuredNativeStore() {
		super();
		addSupportedIsolationLevel( IsolationLevels.READ_COMMITTED );
	}

	public SecuredNativeStore( File dataDir ) {
		super( dataDir );
	}

	public SecuredNativeStore( File dataDir, String tripleIndexes ) {
		super( dataDir, tripleIndexes );
	}

	@Override
	protected CloseableIteration<? extends Statement, IOException> createStatementIterator( Resource subj, URI pred, Value obj, boolean includeInferred, boolean readTransaction, Resource... contexts ) throws IOException {
		CloseableIteration<? extends Statement, IOException> originalIteration = super.createStatementIterator( subj, pred, obj, includeInferred, readTransaction, contexts );

		return new SecuredRepositoryResult( originalIteration );
	}

	public class SecuredRepositoryResult implements CloseableIteration<Statement, IOException> {
		private CloseableIteration<? extends Statement, IOException> originalIteration;
		private Statement nextStatement;

		public SecuredRepositoryResult( CloseableIteration<? extends Statement, IOException> originalIteration ) {
			this.originalIteration = originalIteration;
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

				if ( ! this.canBeAccess( statement ) ) continue;

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
			if ( ! this.hasNext() ) return;

			this.originalIteration.remove();
			this.nextStatement = null;
		}

		private boolean canBeAccess( Statement statement ) {
			if ( ! RepositorySecuritySwitch.isEnabled() ) return true;

			RepositorySecuritySwitch.disable();

			boolean allowed = true;

			dance:
			for ( RepositorySecurityAccessGranter accessGranter : RepositorySecurityAccessGrantersHolder.getInstance().getAccessGranters() ) {
				switch ( accessGranter.canAccess( statement ) ) {
					case GRANT:
						allowed = true;
						break dance;
					case ABSTAIN:
						break;
					case DENY:
						allowed = false;
						break dance;
				}
			}

			RepositorySecuritySwitch.enable();

			return allowed;
		}
	}
}
