package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import org.eclipse.rdf4j.model.IRI;

import java.util.Set;

/**
 * add ldap server container to apps
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o9o1 extends AbstractUpdateAction {

	@Override
	public void execute() throws Exception {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runInPlatformContext( () -> createLDAPContainer( app ) );
		}
	}

	private void createLDAPContainer(App app){
		IRI appIRI = app.getIRI();
		String ldapString = Vars.getInstance().getAppLDAPServerContainer();
		IRI containerIRI = IRIUtil.createChildIRI( appIRI,ldapString );
		RDFResource backupsResource = new RDFResource( containerIRI );
		BasicContainer backupsContainer = BasicContainerFactory.getInstance().create( backupsResource );
		containerRepository.createChild( app.getIRI(), backupsContainer );
		aclRepository.createACL( backupsContainer.getIRI() );
	}
}
