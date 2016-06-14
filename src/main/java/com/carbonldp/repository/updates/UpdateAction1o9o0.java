package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.util.URIUtil;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o9o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
				if ( sourceRepository.exists( IRIUtil.createChildIRI( app.getRootContainerIRI(), Vars.getInstance().getAppTicketsContainer() ) ) ) return;
				appTokensRepository.createTicketsContainer( app.getRootContainerIRI() );
			} );
		}
	}
}
