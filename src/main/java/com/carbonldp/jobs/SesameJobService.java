package com.carbonldp.jobs;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.ImportLDAPAgentsJob;
import com.carbonldp.authentication.ImportLDAPAgentsJobFactory;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.web.exceptions.ForbiddenException;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.ForbiddenException;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
public class SesameJobService extends AbstractSesameLDPService implements JobService {
	private ContainerService containerService;
	private RDFSourceService sourceService;
	private ExecutionService executionService;
	private RDFResourceRepository resourceRepository;
	private AppRoleRepository appRoleRepository;
	private PermissionEvaluator permissionEvaluator;
	protected PermissionEvaluator permissionEvaluator;

	@Override
	public void create( IRI targetIRI, Job job ) {
		validate( job );
		containerService.createChild( targetIRI, job );
	}

	@Override
	public void createExecution( IRI jobIRI, Execution execution ) {
		containerService.createChild( jobIRI, execution );
		IRI executionQueueLocation = resourceRepository.getIRI( jobIRI, JobDescription.Property.EXECUTION_QUEUE_LOCATION );
		executionService.enqueue( execution.getIRI(), executionQueueLocation );
	}

	private void validate( Job job ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new IllegalArgumentException( "The authentication token isn't supported." );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;

		List<Infraction> infractions = new ArrayList<>();
		JobDescription.Type jobType = JobFactory.getInstance().getJobType( job );
		if ( jobType == null )
			throw new InvalidResourceException( new Infraction( 0x2001, "rdf.type", "job type" ) );
		switch ( jobType ) {
			case EXPORT_BACKUP_JOB:
				infractions = ExportBackupJobFactory.getInstance().validate( job );
				break;
			case IMPORT_BACKUP_JOB:
				infractions = ImportBackupJobFactory.getInstance().validate( job );
				checkPermissionsOverTheBackup( job, agentAuthenticationToken );
				break;
			case IMPORT_LDAP_AGENTS_JOB:
				infractions = ImportLDAPAgentsJobFactory.getInstance().validate( job );
				checkPermissionsOverTheLDAP( job, agentAuthenticationToken );
				checkPermissionsOverTheAppRole( job, agentAuthenticationToken );
				break;
			default:
				infractions.add( new Infraction( 0x2001, "rdf.type", "job type" ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void checkPermissionsOverTheBackup( Job job, AgentAuthenticationToken agentAuthenticationToken ) {
		ImportBackupJob importBackupJob = new ImportBackupJob( job );
		IRI backupIRI = importBackupJob.getBackup();
		if ( ! sourceService.exists( backupIRI ) ) throw new InvalidResourceException( new Infraction( 0x2011, "iri", backupIRI.stringValue() ) );
		validateReadDocument( agentAuthenticationToken, backupIRI );

	}

	private void checkPermissionsOverTheLDAP( Job job, AgentAuthenticationToken agentAuthenticationToken ) {
		ImportLDAPAgentsJob importLDAPAgentsJob = new ImportLDAPAgentsJob( job );
		IRI ldapIRI = importLDAPAgentsJob.getLDAPServerIRI();
		validateReadDocument( agentAuthenticationToken, ldapIRI );
	}

	private void checkPermissionsOverTheAppRole( Job job, AgentAuthenticationToken agentAuthenticationToken ) {
		ImportLDAPAgentsJob importLDAPAgentsJob = new ImportLDAPAgentsJob( job );
		IRI defaultAppRole = importLDAPAgentsJob.getDefaultAppRoleIRI();
		if ( defaultAppRole == null ) return;
		Set<AppRole> appRoles = agentAuthenticationToken.getAppRoles();

		Set<IRI> parentsRoles = appRoleRepository.getParentsIRI( defaultAppRole );
		parentsRoles.add( defaultAppRole );

		boolean isParent = false;
		for ( AppRole appRole : appRoles ) {
			if ( parentsRoles.contains( appRole.getSubject() ) ) {
				isParent = true;
				break;
			}
		}
		if ( ! isParent ) {
			Map<String, String> errorMessage = new HashMap<>();
			errorMessage.put( "action", "give AppRole" );
			errorMessage.put( "uri", "this agents" );
			throw new ForbiddenException( new Infraction( 0x7001, errorMessage ) );
		}

	}

	private void validateReadDocument( AgentAuthenticationToken agentAuthenticationToken, IRI resourceIRI ) {
		if ( resourceIRI == null ) return;
		if ( ! sourceService.exists( resourceIRI ) ) {
			throw new BadRequestException( new Infraction( 0x2011, "iri", resourceIRI.stringValue() ) );
		}
		if ( ! permissionEvaluator.hasPermission( agentAuthenticationToken, resourceIRI, ACEDescription.Permission.READ ) ) {
			Map<String, String> errorMessage = new HashMap<>();
			errorMessage.put( "action", "read" );
			errorMessage.put( "uri", resourceIRI.stringValue() );
			throw new ForbiddenException( new Infraction( 0x7001, errorMessage ) );
		}
	}

	@Override
	public Job get( IRI targetIRI ) {
		return new Job( sourceService.get( targetIRI ) );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setExecutionService( ExecutionService executionService ) {
		this.executionService = executionService;
	}

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) {
		this.resourceRepository = resourceRepository;
	}

	@Autowired
	public void setPermissionEvaluator( PermissionEvaluator permissionEvaluator ) {
		this.permissionEvaluator = permissionEvaluator;
	}

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) {
		this.appRoleRepository = appRoleRepository;
	}

	@Autowired
	public void setPermissionEvaluator( PermissionEvaluator permissionEvaluator ) {
		this.permissionEvaluator = permissionEvaluator;
	}
}
