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
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
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
		valueFactory = new ValueFactoryImpl();
	}

	@Override
	public void replace( ACL newAcl ) {
		if ( ! sourceRepository.exists( newAcl.getURI() ) ) throw new ResourceDoesntExistException();
		validateProperties( newAcl );

		DateTime modifiedTime = DateTime.now();

		//first boolean tell us whether it is inheritable(false) or direct(true), the second boolean tells us whether the Ace is for granting(true) or denying(false) permissions
		Map<Boolean, Map<Boolean, Set<ACE>>> permissions = getPermissions( get( newAcl.getURI() ) );
		Map<Boolean, Map<Boolean, Set<ACE>>> newPermissions = getPermissions( newAcl );

		//key tells us whether we are going to add or delete the permission
		Set<ACE> permissionsToModify = comparePermissions( permissions, newPermissions );

		validate( permissionsToModify, newAcl.getAccessTo() );
		ACL aclToPersist = ACLFactory.create( newAcl.getURI(), newAcl.getAccessTo() );
		aclToPersist = addPermissions( newPermissions, aclToPersist );

		aclRepository.replace( aclToPersist );

		sourceRepository.touch( newAcl.getURI(), modifiedTime );
	}

	private void validateProperties( ACL newAcl ) {
		List<Infraction> infractions = ACLFactory.getInstance().validate( newAcl );

		Set<ACE> aces = ACEFactory.getInstance().get( newAcl.getBaseModel(), newAcl.getACEntries() );
		aces.addAll( ACEFactory.getInstance().get( newAcl.getBaseModel(), newAcl.getInheritableEntries() ) );
		for ( ACE ace : aces ) {
			infractions.addAll( ACEFactory.getInstance().validate( ace ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void validate( Set<ACE> permissionsToModify, URI accessTo ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new IllegalArgumentException( "The authentication token isn't supported." );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;

		for ( ACE ace : permissionsToModify ) {

			//check if the logged one is parent of the modified one
			ACEDescription.SubjectType subjectClass = RDFNodeUtil.findByURI( ace.getSubjectClass(), ACEDescription.SubjectType.class );
			if ( subjectClass == null ) throw new StupidityException( "there's no subjectClass property in the ACE" );
			switch ( subjectClass ) {
				case AGENT:
					throw new NotImplementedException();
				case APP_ROLE:
					boolean isParent = false;
					App app = AppContextHolder.getContext().getApplication();
					if ( app == null ) throw new RuntimeException( "unable to add an app role permission on a platform" );
					Set<AppRole> appRoles = agentAuthenticationToken.getAppRoles( app.getURI() );
					URI appRoleToModify = ace.getSubjects().iterator().next();
					Set<URI> parentsRoles = appRoleRepository.getParents( appRoleToModify );
					for ( AppRole appRole : appRoles ) {
						if ( parentsRoles.contains( appRole.getSubject() ) ) {
							isParent = true;
							break;
						}
					}
					if ( ! isParent ) throw new BadCredentialsException( "you don't have permissions to modify this resource" );
					break;
				case PLATFORM_ROLE:

			}

			// check if the logged one has the permissions that he want to modify
			for ( ACEDescription.Permission permission : ace.getPermissions() )
				if ( permissionEvaluator.hasPermission( agentAuthenticationToken, accessTo, permission ) )
					throw new BadCredentialsException( "you don't have permissions to modify this resource" );
		}
	}

	private Set<ACE> comparePermissions( Map<Boolean, Map<Boolean, Set<ACE>>> permissions, Map<Boolean, Map<Boolean, Set<ACE>>> newPermissions ) {
		Set<ACE> permissionsToModify = new LinkedHashSet<>();

		permissionsToModify.addAll( compareAces( permissions.get( true ).get( true ), newPermissions.get( true ).get( true ) ) );

		permissionsToModify.addAll( compareAces( permissions.get( true ).get( false ), newPermissions.get( true ).get( false ) ) );

		permissionsToModify.addAll( compareAces( permissions.get( false ).get( true ), newPermissions.get( false ).get( true ) ) );

		permissionsToModify.addAll( compareAces( permissions.get( false ).get( false ), newPermissions.get( false ).get( false ) ) );

		return permissionsToModify;
	}

	private Set<ACE> compareAces( Set<ACE> aces, Set<ACE> newAces ) {
		Set<ACE> permissionsToModify = new LinkedHashSet<>();

		for ( ACE newAce : newAces ) {
			boolean addAllPermissions = true;
			for ( ACE ace : aces ) {
				addAllPermissions = true;
				if ( ace.getSubjects().iterator().next().equals( newAce.getSubjects().iterator().next() ) ) {
					ACE aceToAdd = new ACE( new LinkedHashModel(), valueFactory.createBNode() );

					for ( ACEDescription.Permission permission : newAce.getPermissions() ) {
						if ( ace.getPermissions().contains( permission ) ) {
							ace.getPermissions().remove( permission );
						} else {
							aceToAdd.getPermissions().add( permission );
						}
					}

					if ( ! ace.getPermissions().isEmpty() ) {
						permissionsToModify.add( ace );
					}
					if ( ! aceToAdd.getPermissions().isEmpty() ) {
						newAce.addType( ACEDescription.Resource.CLASS.getURI() );
						newAce.setSubjectClass( ace.getSubjectClass() );
						newAce.addSubject( ace.getSubjects().iterator().next() );
						newAce.setGranting( ace.isGranting() );
						permissionsToModify.add( aceToAdd );
					}

					aces.remove( ace );
					addAllPermissions = false;
					break;
				}
			}
			if ( addAllPermissions ) {
				permissionsToModify.add( newAce );
			}
		}
		if ( ! aces.isEmpty() ) {
			permissionsToModify.addAll( aces );
		}
		return permissionsToModify;
	}

	private Map<Boolean, Map<Boolean, Set<ACE>>> getPermissions( ACL acl ) {
		Set<ACE> aces = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getACEntries() );
		Set<ACE> inheritableAces = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getInheritableEntries() );
		Map<Boolean, Map<Boolean, Set<ACE>>> permissions = new LinkedHashMap<>();
		permissions.put( true, updateACEList( aces ) );
		permissions.put( false, updateACEList( inheritableAces ) );

		return permissions;
	}

	private ACL addPermissions( Map<Boolean, Map<Boolean, Set<ACE>>> permissions, ACL acl ) {
		Set<ACE> repeatedAcesGranting = getRepeatedAces( permissions.get( true ).get( false ), permissions.get( false ).get( false ) );
		Set<ACE> repeatedAcesDenying = getRepeatedAces( permissions.get( true ).get( true ), permissions.get( false ).get( true ) );

		Set<ACE> acesDirectGranting = fuseQuadrant( permissions.get( true ).get( true ) );
		Set<ACE> acesDirectDenying = fuseQuadrant( permissions.get( true ).get( false ) );
		Set<ACE> acesInheritableGranting = fuseQuadrant( permissions.get( false ).get( true ) );
		Set<ACE> acesInheritableDenying = fuseQuadrant( permissions.get( false ).get( false ) );
		repeatedAcesGranting = fuseQuadrant( repeatedAcesGranting );
		repeatedAcesDenying = fuseQuadrant( repeatedAcesDenying );

		for ( ACE ace : acesDirectGranting ) {
			acl.addACEntry( ace.getSubject() );
			acl.addAll( ace );
		}
		for ( ACE ace : acesDirectDenying ) {
			acl.addACEntry( ace.getSubject() );
			acl.addAll( ace );
		}
		for ( ACE ace : acesInheritableGranting ) {
			acl.addInheritableEntry( ace.getSubject() );
			acl.addAll( ace );
		}
		for ( ACE ace : acesInheritableDenying ) {
			acl.addInheritableEntry( ace.getSubject() );
			acl.addAll( ace );
		}
		for ( ACE ace : repeatedAcesGranting ) {
			acl.addACEntry( ace.getSubject() );
			acl.addInheritableEntry( ace.getSubject() );
			acl.addAll( ace );
		}
		for ( ACE ace : repeatedAcesDenying ) {
			acl.addACEntry( ace.getSubject() );
			acl.addInheritableEntry( ace.getSubject() );
			acl.addAll( ace );
		}

		return acl;

	}

	private Set<ACE> getRepeatedAces( Set<ACE> directAces, Set<ACE> inheritableAces ) {
		Set<ACE> repeatedAces = new LinkedHashSet<>();
		for ( Iterator<ACE> directIterator = directAces.iterator(); directIterator.hasNext(); ) {
			ACE directAce = directIterator.next();
			URI directSubject = directAce.getSubjects().iterator().next();
			for ( Iterator<ACE> inheritableIterator = inheritableAces.iterator(); inheritableIterator.hasNext(); ) {
				ACE inheritableAce = inheritableIterator.next();
				URI inheritableSubject = inheritableAce.getSubjects().iterator().next();
				if ( ! directSubject.equals( inheritableSubject ) ) continue;
				if ( ! hasSamePermissions( directAce, inheritableAce ) ) continue;
				repeatedAces.add( directAce );
				directIterator.remove();
				inheritableIterator.remove();
			}
		}
		return repeatedAces;
	}

	private Set<ACE> fuseQuadrant( Set<ACE> rawAces ) {
		Set<ACE> fusedAces = new LinkedHashSet<>();
		while ( ! rawAces.isEmpty() ) {
			boolean isDuplicated = false;
			ACE rawAce = rawAces.iterator().next();
			for ( ACE ace : fusedAces ) {
				if ( ! hasSamePermissions( ace, rawAce ) ) continue;
				ace.addSubject( rawAce.getSubjects().iterator().next() );
				isDuplicated = true;
				break;
			}
			if ( ! isDuplicated )
				fusedAces.add( rawAce );
			rawAces.remove( rawAce );
		}
		return fusedAces;
	}

	private boolean hasSamePermissions( ACE ace1, ACE ace2 ) {
		Set<ACEDescription.Permission> ace1Permissions = ace1.getPermissions();
		Set<ACEDescription.Permission> ace2Permissions = ace2.getPermissions();
		if ( ace1Permissions.size() != ace2Permissions.size() ) return false;
		for ( ACEDescription.Permission permission : ace2Permissions )
			if ( ! ace1Permissions.contains( permission ) ) return false;

		return true;
	}

	private Map<Boolean, Set<ACE>> updateACEList( Set<ACE> aces ) {
		Set<ACE> grantingAces = new LinkedHashSet<>();
		Set<ACE> denyingAces = new LinkedHashSet<>();
		for ( ACE ace : aces ) {
			grantingAces = updateAces( grantingAces, ace, true );
			denyingAces = updateAces( denyingAces, ace, false );
		}
		Map<Boolean, Set<ACE>> newAcesMap = new LinkedHashMap<>();
		newAcesMap.put( true, grantingAces );
		newAcesMap.put( false, denyingAces );
		return newAcesMap;
	}

	private Set<ACE> updateAces( Set<ACE> aces, ACE ace, boolean granting ) {
		if ( ace.isGranting() != granting ) return aces;

		Set<URI> subjects = ace.getSubjects();
		Map<URI, ACE> newACESubjects = getACESubjects( aces );

		for ( URI subject : subjects ) {
			ACE newAce = newACESubjects.get( subject );
			if ( newAce == null ) {
				newAce = new ACE( new LinkedHashModel(), valueFactory.createBNode() );
				newAce.addType( ACEDescription.Resource.CLASS.getURI() );
				newAce.setSubjectClass( ace.getSubjectClass() );
				newAce.addSubject( subject );
				newAce.setGranting( granting );
				ace.getPermissions().forEach( newAce::addPermission );

				aces.add( newAce );
			} else {ace.getPermissions().forEach( newAce::addPermission );}
		}
		return aces;
	}

	private Map<URI, ACE> getACESubjects( Set<ACE> aces ) {
		Map<URI, ACE> aceSubjects = new LinkedHashMap<>();
		for ( ACE ace : aces ) {aceSubjects.put( ace.getSubjects().iterator().next(), ace );}
		return aceSubjects;
	}

	@Override
	public ACL get( URI aclURI ) {
		return new ACL( sourceRepository.get( aclURI ), aclURI );
	}
}
