package com.carbonldp.authentication.ldapServer;

import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.apps.App;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.authentication.ImportLDAPAgentsJob;
import com.carbonldp.authentication.ImportLDAPAgentsJobDescription;
import com.carbonldp.authentication.ldapServer.app.LDAPServerService;
import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.jobs.*;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class ImportLDAPAgentsJobExecutor implements TypedJobExecutor {
	private RDFSourceService sourceService;
	private LDAPServerService ldapServerService;
	private TransactionWrapper transactionWrapper;
	private AppRoleService appRoleService;

	@Override
	public boolean supports( JobDescription.Type jobType ) {
		return jobType == JobDescription.Type.IMPORT_LDAP_AGENTS_JOB;
	}

	@Override
	public void execute( App app, Job job, Execution execution ) {
		if ( ! job.hasType( ImportLDAPAgentsJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ImportLDAPAgentsJobDescription.Resource.CLASS.getIRI().stringValue() ) );
		ImportLDAPAgentsJob importLDAPAgentsJob = new ImportLDAPAgentsJob( job );
		IRI ldapServerIRI = importLDAPAgentsJob.getLDAPServerIRI();
		LDAPServer ldapServer = new LDAPServer( sourceService.get( ldapServerIRI ) );
		IRI defaultAppRole = importLDAPAgentsJob.getDefaultAppRoleIRI();

		transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
			List<LDAPAgent> agents = ldapServerService.registerLDAPAgents( ldapServer, importLDAPAgentsJob.getLDAPUsernameFields(), app );
			if ( defaultAppRole == null ) return;

			IRI roleAgentsContainerIRI = appRoleService.getAgentsContainerIRI( defaultAppRole );
			Set<IRI> agentsIRI = agents.stream().map( LDAPAgent::getIRI ).collect( Collectors.toSet() );
			appRoleService.addAgents( roleAgentsContainerIRI, agentsIRI );
		} );

	}

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}

	@Autowired
	public void setLdapServerService( LDAPServerService ldapServerService ) {
		this.ldapServerService = ldapServerService;
	}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {
		this.transactionWrapper = transactionWrapper;
	}

	@Autowired
	@Qualifier( "appRoleService" )
	public void setAppRoleService( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}
