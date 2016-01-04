package com.carbonldp.authorization.acl;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

public class SesameACLService extends AbstractSesameLDPService implements ACLService {
	ValueFactory valueFactory;
	PermissionEvaluator permissionEvaluator;
	AppRoleRepository appRoleRepository;

	public SesameACLService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, PermissionEvaluator permissionEvaluator, AppRoleRepository appRoleRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );

		this.permissionEvaluator = permissionEvaluator;
		this.appRoleRepository = appRoleRepository;
		this.valueFactory = new ValueFactoryImpl();
	}

	@Override
	public ACL get( URI aclURI ) {
		return new ACL( sourceRepository.get( aclURI ), aclURI );
	}

	@Override
	public void replace( ACL newACL ) {
		URI aclURI = newACL.getURI();

		if ( ! sourceRepository.exists( aclURI ) ) throw new ResourceDoesntExistException();
		validateACL( newACL );

		ACL oldACL = get( aclURI );

		Map<Subject, SubjectPermissions> oldACLSubjects = getACLSubjects( oldACL );
		Map<Subject, SubjectPermissions> newACLSubjects = getACLSubjects( newACL );

		Map<ModifyType, Map<Subject, SubjectPermissions>> subjectPermissionsToModify = getSubjectPermissionsToModify( oldACLSubjects, newACLSubjects );

		validateModifications( subjectPermissionsToModify, newACL.getAccessTo() );

		ACL aclToPersist = generateACL( aclURI, newACL.getAccessTo(), newACLSubjects );

		aclRepository.replace( aclToPersist );

		sourceRepository.touch( aclURI );
	}

	// TODO: compact the resulting ACL
	private ACL generateACL( URI aclURI, URI accessTo, Map<Subject, SubjectPermissions> subjectPermissionsToModify ) {
		ACL acl = ACLFactory.create( aclURI, accessTo );

		for ( Subject subject : subjectPermissionsToModify.keySet() ) {
			for ( InheritanceType inheritanceType : subjectPermissionsToModify.get( subject ).keySet() ) {
				for ( PermissionType permissionType : subjectPermissionsToModify.get( subject ).get( inheritanceType ).keySet() ) {
					ACEDescription.SubjectType subjectType = RDFNodeUtil.findByURI( subject.getSubjectClass(), ACEDescription.SubjectType.class );
					Set<ACEDescription.Permission> permissions = subjectPermissionsToModify.get( subject ).get( inheritanceType ).get( permissionType );
					if ( permissions.isEmpty() || permissions.size() == 0 ) continue;

					boolean granting = permissionType == PermissionType.GRANTING;

					ACE ace = ACEFactory.getInstance().create( acl, subjectType, subject.getURI(), permissions, granting );

					if ( inheritanceType == InheritanceType.DIRECT ) {
						acl.addACEntry( ace.getSubject() );
					} else if ( inheritanceType == InheritanceType.INHERITABLE ) {
						acl.addInheritableEntry( ace.getSubject() );
					}
				}
			}
		}

		return acl;
	}

	private Map<ModifyType, Map<Subject, SubjectPermissions>> getSubjectPermissionsToModify( Map<Subject, SubjectPermissions> oldACLSubjects, Map<Subject, SubjectPermissions> newACLSubjects ) {
		Map<ModifyType, Map<Subject, SubjectPermissions>> subjectPermissionsToModify = new HashMap<>();
		subjectPermissionsToModify.put( ModifyType.ADD, new HashMap<>() );
		subjectPermissionsToModify.put( ModifyType.REMOVE, new HashMap<>() );
		Set<Subject> subjects = new HashSet<>();
		subjects.addAll( oldACLSubjects.keySet() );
		subjects.addAll( newACLSubjects.keySet() );

		for ( Subject subject : subjects ) {
			SubjectPermissions newSubjectPermissions = newACLSubjects.get( subject );
			SubjectPermissions oldSubjectPermissions = oldACLSubjects.get( subject );

			if ( ! oldACLSubjects.containsKey( subject ) ) {
				subjectPermissionsToModify.get( ModifyType.ADD ).put( subject, newSubjectPermissions );
				continue;
			}

			if ( ! newACLSubjects.containsKey( subject ) ) {
				subjectPermissionsToModify.get( ModifyType.REMOVE ).put( subject, oldSubjectPermissions );
				continue;
			}

			for ( InheritanceType inheritanceType : oldSubjectPermissions.keySet() ) {
				for ( PermissionType permissionType : oldSubjectPermissions.get( inheritanceType ).keySet() ) {
					Set<ACEDescription.Permission> oldPermissions = oldSubjectPermissions.get( inheritanceType ).get( permissionType );
					Set<ACEDescription.Permission> newPermissions = newSubjectPermissions.get( inheritanceType ).get( permissionType );

					Set<ACEDescription.Permission> addedPermissions = new HashSet<>( newPermissions );
					Set<ACEDescription.Permission> removedPermissions = new HashSet<>( oldPermissions );

					addedPermissions.removeAll( oldPermissions );
					removedPermissions.removeAll( newPermissions );

					if ( ! addedPermissions.isEmpty() ) {
						Map<Subject, SubjectPermissions> addedSubjectPermissions = subjectPermissionsToModify.get( ModifyType.ADD );
						if ( ! addedSubjectPermissions.containsKey( subject ) ) {
							addedSubjectPermissions.put( subject, new SubjectPermissions() );
						}
						addedSubjectPermissions.get( subject ).get( inheritanceType ).get( permissionType ).addAll( addedPermissions );
					}

					if ( ! removedPermissions.isEmpty() ) {
						Map<Subject, SubjectPermissions> removedSubjectPermissions = subjectPermissionsToModify.get( ModifyType.REMOVE );
						if ( ! removedSubjectPermissions.containsKey( subject ) ) {
							removedSubjectPermissions.put( subject, new SubjectPermissions() );
						}
						removedSubjectPermissions.get( subject ).get( inheritanceType ).get( permissionType ).addAll( removedPermissions );
					}
				}
			}
		}

		return subjectPermissionsToModify;
	}

	private Map<Subject, SubjectPermissions> getACLSubjects( ACL acl ) {
		Map<Subject, SubjectPermissions> aclSubjects = new HashMap<>();

		Set<ACE> aces = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getACEntries(), acl.getURI() );
		addACEsSubjects( aces, InheritanceType.DIRECT, aclSubjects );

		Set<ACE> inheritableAces = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getInheritableEntries(), acl.getURI() );
		addACEsSubjects( inheritableAces, InheritanceType.INHERITABLE, aclSubjects );

		return aclSubjects;
	}

	private void addACEsSubjects( Set<ACE> aces, InheritanceType inheritanceType, Map<Subject, SubjectPermissions> aclSubjects ) {
		for ( ACE ace : aces ) {
			for ( URI aceSubject : ace.getSubjects() ) {
				Subject subject = new Subject( aceSubject, ace.getSubjectClass() );
				SubjectPermissions subjectPermissions;
				if ( ! aclSubjects.containsKey( subject ) ) {
					aclSubjects.put( subject, new SubjectPermissions() );
				}
				subjectPermissions = aclSubjects.get( subject );

				PermissionType permissionType = ace.isGranting() ? PermissionType.GRANTING : PermissionType.DENYING;

				Set<ACEDescription.Permission> permissions = subjectPermissions.get( inheritanceType ).get( permissionType );
				permissions.addAll( ace.getPermissions() );
			}
		}
	}

	private void validateACL( ACL newAcl ) {
		List<Infraction> infractions = ACLFactory.getInstance().validate( newAcl );

		Set<ACE> aces = ACEFactory.getInstance().get( newAcl.getBaseModel(), newAcl.getACEntries(), newAcl.getURI() );
		aces.addAll( ACEFactory.getInstance().get( newAcl.getBaseModel(), newAcl.getInheritableEntries(), newAcl.getURI() ) );
		for ( ACE ace : aces ) {
			infractions.addAll( ACEFactory.getInstance().validate( ace ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void validateModifications( Map<ModifyType, Map<Subject, SubjectPermissions>> subjectPermissionsToModify, URI accessTo ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new IllegalArgumentException( "The authentication token isn't supported." );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;

		Set<Subject> affectedSubjects = getAffectedSubjects( subjectPermissionsToModify );
		Set<ACEDescription.Permission> affectedPermissions = getAffectedPermissions( subjectPermissionsToModify );

		for ( Subject subject : affectedSubjects ) {
			ACEDescription.SubjectType subjectClass = RDFNodeUtil.findByURI( subject.getSubjectClass(), ACEDescription.SubjectType.class );
			if ( subjectClass == null ) throw new StupidityException( "There's no subjectClass property in the ACE" );
			switch ( subjectClass ) {
				case AGENT:
					throw new NotImplementedException();
				case APP_ROLE:
					App app = AppContextHolder.getContext().getApplication();
					if ( app == null ) throw new IllegalStateException( "Unable to add an app role permission on the platform context" );

					Set<AppRole> appRoles = agentAuthenticationToken.getAppRoles( app.getURI() );

					URI appRoleToModify = subject.getURI();
					Set<URI> parentsRoles = appRoleRepository.getParentsURI( appRoleToModify );

					boolean isParent = false;
					for ( AppRole appRole : appRoles ) {
						if ( parentsRoles.contains( appRole.getSubject() ) ) {
							isParent = true;
							break;
						}
					}
					if ( ! isParent ) throw new BadCredentialsException( "you don't have permissions to modify this resource" );
					break;
				case PLATFORM_ROLE:
					throw new NotImplementedException();
				default:
					throw new InvalidResourceException( new Infraction( 0x2005, "property", ACEDescription.Property.SUBJECT_CLASS.getURI().stringValue() ) );

			}
		}

		for ( ACEDescription.Permission permission : affectedPermissions ) {
			if ( ! permissionEvaluator.hasPermission( agentAuthenticationToken, accessTo, permission ) ) {
				throw new BadCredentialsException( "You don't have permissions to modify this resource" );
			}
		}
	}

	private Set<Subject> getAffectedSubjects( Map<ModifyType, Map<Subject, SubjectPermissions>> subjectPermissionsToModify ) {
		Set<Subject> affectedSubjects = new HashSet<>();
		for ( ModifyType modifyType : subjectPermissionsToModify.keySet() ) {
			affectedSubjects.addAll( subjectPermissionsToModify.get( modifyType ).keySet() );
		}
		return affectedSubjects;
	}

	private Set<ACEDescription.Permission> getAffectedPermissions( Map<ModifyType, Map<Subject, SubjectPermissions>> subjectPermissionsToModify ) {
		Set<ACEDescription.Permission> affectedPermissions = new HashSet<>();
		for ( ModifyType modifyType : subjectPermissionsToModify.keySet() ) {
			for ( Subject subject : subjectPermissionsToModify.get( modifyType ).keySet() ) {
				for ( InheritanceType inheritanceType : subjectPermissionsToModify.get( modifyType ).get( subject ).keySet() ) {
					for ( PermissionType permissionType : subjectPermissionsToModify.get( modifyType ).get( subject ).get( inheritanceType ).keySet() ) {
						affectedPermissions.addAll( subjectPermissionsToModify.get( modifyType ).get( subject ).get( inheritanceType ).get( permissionType ) );
					}
				}
			}
		}
		return affectedPermissions;
	}

	public enum InheritanceType {
		INHERITABLE,
		DIRECT
	}

	public enum PermissionType {
		GRANTING,
		DENYING
	}

	public enum ModifyType {
		ADD,
		REMOVE
	}

	public static class SubjectPermissions extends HashMap<InheritanceType, Map<PermissionType, Set<ACEDescription.Permission>>> {
		public SubjectPermissions() {
			super();

			this.put( InheritanceType.DIRECT, new HashMap<>() );
			this.get( InheritanceType.DIRECT ).put( PermissionType.GRANTING, new HashSet<>() );
			this.get( InheritanceType.DIRECT ).put( PermissionType.DENYING, new HashSet<>() );

			this.put( InheritanceType.INHERITABLE, new HashMap<>() );
			this.get( InheritanceType.INHERITABLE ).put( PermissionType.GRANTING, new HashSet<>() );
			this.get( InheritanceType.INHERITABLE ).put( PermissionType.DENYING, new HashSet<>() );
		}
	}

	public static class Subject {
		private URI uri;
		private URI subjectClass;

		public Subject( URI uri, URI subjectClass ) {
			this.uri = uri;
			this.subjectClass = subjectClass;
		}

		public URI getURI() {
			return uri;
		}

		public URI getSubjectClass() {
			return subjectClass;
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) return true;
			if ( o == null || getClass() != o.getClass() ) return false;

			Subject that = (Subject) o;

			if ( uri != null ? ! uri.equals( that.uri ) : that.uri != null ) return false;
			return ! ( subjectClass != null ? ! subjectClass.equals( that.subjectClass ) : that.subjectClass != null );

		}

		@Override
		public int hashCode() {
			int result = uri != null ? uri.hashCode() : 0;
			result = 31 * result + ( subjectClass != null ? subjectClass.hashCode() : 0 );
			return result;
		}
	}
}