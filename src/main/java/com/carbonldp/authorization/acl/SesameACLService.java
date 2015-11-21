package com.carbonldp.authorization.acl;

import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.RDFResourceUtil;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.*;
import java.util.stream.Collectors;

public class SesameACLService extends AbstractSesameLDPService implements ACLService {
	ValueFactory valueFactory;

	public SesameACLService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		valueFactory = new ValueFactoryImpl();
	}

	@Override
	public void replace( ACL newAcl ) {
		if ( ! sourceRepository.exists( newAcl.getURI() ) ) throw new ResourceDoesntExistException();

		DateTime modifiedTime = DateTime.now();

		//first boolean tell us if it is inheritable(false) or direct(true), the second boolean tells us if the Ace is for granting(true) or denying(false) permissions
		Map<Boolean, Map<Boolean, Set<ACE>>> permissions = getPermissions( get( newAcl.getURI() ) );
		Map<Boolean, Map<Boolean, Set<ACE>>> newPermissions = getPermissions( newAcl );

		Map<Boolean, Set<ACE>> permissionsToModify = comparePermissions( permissions, newPermissions );

		substract( originalAcl.getURI(), resourceViewsToDelete );
		add( originalAcl.getURI(), resourceViewsToAdd );

		sourceRepository.touch( newAcl.getURI(), modifiedTime );
	}

	private Map<Boolean, Set<ACE>> comparePermissions( Map<Boolean, Map<Boolean, Set<ACE>>> permissions, Map<Boolean, Map<Boolean, Set<ACE>>> newPermissions ) {
		Map<Boolean, Set<ACE>> partialPermissionsToModify;
		Map<Boolean, Set<ACE>> permissionsToModify;

		permissionsToModify = compareAces( permissions.get( true ).get( true ), newPermissions.get( true ).get( true ) );

		partialPermissionsToModify = compareAces( permissions.get( true ).get( false ), newPermissions.get( true ).get( false ) );
		permissionsToModify.get( true ).addAll( partialPermissionsToModify.get( true ) );
		permissionsToModify.get( false ).addAll( partialPermissionsToModify.get( false ) );
		partialPermissionsToModify = compareAces( permissions.get( false ).get( true ), newPermissions.get( false ).get( true ) );
		permissionsToModify.get( true ).addAll( partialPermissionsToModify.get( true ) );
		permissionsToModify.get( false ).addAll( partialPermissionsToModify.get( false ) );
		partialPermissionsToModify = compareAces( permissions.get( false ).get( false ), newPermissions.get( false ).get( false ) );
		permissionsToModify.get( true ).addAll( partialPermissionsToModify.get( true ) );
		permissionsToModify.get( false ).addAll( partialPermissionsToModify.get( false ) );

		return permissionsToModify;
	}

	private Map<Boolean, Set<ACE>> compareAces( Set<ACE> aces, Set<ACE> newAces ) {
		Map<Boolean, Set<ACE>> permissionsToModify = new LinkedHashMap<>();
		permissionsToModify.put( true, new LinkedHashSet<>() );
		permissionsToModify.put( false, new LinkedHashSet<>() );

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
						permissionsToModify.get( false ).add( ace );
					}
					if ( ! aceToAdd.getPermissions().isEmpty() ) {
						newAce.addType( ACEDescription.Resource.CLASS.getURI() );
						newAce.setSubjectClass( ace.getSubjectClass() );
						newAce.addSubject( ace.getSubjects().iterator().next() );
						newAce.setGranting( ace.isGranting() );
						permissionsToModify.get( true ).add( aceToAdd );
					}

					aces.remove( ace );
					addAllPermissions = false;
					break;
				}
			}
			if ( addAllPermissions ) {
				permissionsToModify.get( true ).add( newAce );
			}
		}
		if ( ! aces.isEmpty() ) {
			permissionsToModify.get( false ).addAll( aces );
		}
		return permissionsToModify;
	}

	private Map<Boolean, Map<Boolean, Set<ACE>>> getPermissions( ACL acl ) {
		Set<ACE> aces = ACEFactory.get( acl.getBaseModel(), acl.getACEntries() );
		Set<ACE> inheritableAces = ACEFactory.get( acl.getBaseModel(), acl.getInheritableEntries() );
		Map<Boolean, Map<Boolean, Set<ACE>>> permissions = new LinkedHashMap<>();
		permissions.put( true, updateACEList( aces ) );
		permissions.put( false, updateACEList( inheritableAces ) );

		return permissions;
	}

	private ACL createACL( Map<Boolean, Map<Boolean, Set<ACE>>> permissions ) {

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
