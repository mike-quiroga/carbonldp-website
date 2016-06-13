package com.carbonldp.repository.updates;

import com.carbonldp.apps.App;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.IRI;

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
				IRI authTokenContainerIRI = ticketService.getTicketsContainerIRI();
				RDFResource rdfResource = new RDFResource( authTokenContainerIRI );
				BasicContainer ticketContainer = BasicContainerFactory.getInstance().create( rdfResource );
				containerRepository.createChild( app.getRootContainerIRI(), ticketContainer );
			} );
		}
	}
}
