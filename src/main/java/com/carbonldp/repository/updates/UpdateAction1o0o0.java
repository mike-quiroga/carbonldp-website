package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class UpdateAction1o0o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "platform-default.trig";

	@Override
	public void execute() throws Exception {
		emptyRepository();
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
	}

	protected void emptyRepository() {
		transactionWrapper.runInPlatformContext( () -> {
			try {
				connectionFactory.getConnection().remove( (Resource) null, null, null );
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}
		} );

	}
}
