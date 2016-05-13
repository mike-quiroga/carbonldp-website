package com.carbonldp.authentication.LDAP;

import com.carbonldp.apps.App;
import com.carbonldp.authentication.ImportLDAPAgentsJob;
import com.carbonldp.authentication.LDAP.app.LDAPServerService;
import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.jobs.*;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class ImportLDAPAgentsJobExecutor implements TypedJobExecutor {
	RDFSourceService sourceService;
	LDAPServerService ldapServerService;

	@Override
	public boolean supports( JobDescription.Type jobType ) {return jobType == JobDescription.Type.IMPORT_LDAP_AGENTS_JOB;}

	@Override
	public void execute( App app, Job job, Execution execution ) {
		if ( ! job.hasType( ImportBackupJobDescription.Resource.CLASS ) ) throw new JobException( new Infraction( 0x2001, "rdf.type", ImportBackupJobDescription.Resource.CLASS.getIRI().stringValue() ) );
		ImportLDAPAgentsJob importLDAPAgentsJob = new ImportLDAPAgentsJob( job );
		IRI ldapServerIRI = importLDAPAgentsJob.getLDAPServerIRI();
		LDAPServer ldapServer = new LDAPServer( sourceService.get( ldapServerIRI ) );
		ldapServerService.registerLDAPAgents( ldapServer, importLDAPAgentsJob.getLDAPUsernameFields() );

	}

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}

	@Autowired
	public void setLdapServerService( LDAPServerService ldapServerService ) {
		this.ldapServerService = ldapServerService;
	}
}
