package org.openrdf.sail.nativerdf;

import com.carbonldp.repository.security.RepositorySecurityAccessGranter;
import com.carbonldp.repository.security.RepositorySecurityAccessGrantersHolder;
import com.carbonldp.repository.security.RepositorySecuritySwitch;
import info.aduna.iteration.CloseableIteration;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.sail.SailException;

import java.io.File;
import java.io.IOException;

/**
 * @author MiguelAraCo
 * @since 0.32.0
 */
@SuppressWarnings( "deprecation" )
public class SecuredNativeSailStore extends NativeSailStore {
	public SecuredNativeSailStore( File dataDir, String tripleIndexes ) throws IOException, SailException {
		super( dataDir, tripleIndexes );
	}

	public SecuredNativeSailStore( File dataDir, String tripleIndexes, boolean forceSync, int valueCacheSize, int valueIDCacheSize, int namespaceCacheSize, int namespaceIDCacheSize ) throws IOException, SailException {
		super( dataDir, tripleIndexes, forceSync, valueCacheSize, valueIDCacheSize, namespaceCacheSize, namespaceIDCacheSize );
	}

	@Override
	CloseableIteration<? extends Statement, SailException> createStatementIterator( Resource subj, org.openrdf.model.URI pred, Value obj, boolean explicit, Resource... contexts ) throws IOException {
		CloseableIteration<? extends Statement, SailException> originalIteration = super.createStatementIterator( subj, pred, obj, explicit, contexts );

		return new SecuredRepositoryResult( originalIteration );
	}

	public class SecuredRepositoryResult implements CloseableIteration<Statement, SailException> {
		private CloseableIteration<? extends Statement, SailException> originalIteration;
		private Statement nextStatement;

		public SecuredRepositoryResult( CloseableIteration<? extends Statement, SailException> originalIteration ) {
			this.originalIteration = originalIteration;
		}

		@Override
		public void close() throws SailException {
			this.originalIteration.close();
		}

		@Override
		public boolean hasNext() throws SailException {
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
		public Statement next() throws SailException {
			if ( ! this.hasNext() ) return null;

			Statement statement = this.nextStatement;
			this.nextStatement = null;
			return statement;
		}

		@Override
		public void remove() throws SailException {
			if ( ! this.hasNext() ) return;

			this.originalIteration.remove();
			this.nextStatement = null;
		}

		private boolean canBeAccess( Statement statement ) {
			if ( ! RepositorySecuritySwitch.isEnabled() ) return true;

			RepositorySecuritySwitch.disable();

			boolean allowed = true;

			dance:
			for ( RepositorySecurityAccessGranter accessGranter : RepositorySecurityAccessGrantersHolder.get() ) {
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
