package com.carbonldp.authorization.acl;

import com.carbonldp.agents.AgentDescription;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.authentication.AbstractAuthenticationToken;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.PlatformPrivilegeDescription;
import com.carbonldp.authorization.PlatformRoleDescription;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.IRI;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

public final class SubjectsRetrievalStrategy {
	private SubjectsRetrievalStrategy() {
		// Meaning non-instantiable
	}

	public static Map<RDFNodeEnum, Set<IRI>> getSubjects( Authentication authentication ) {

		if ( authentication instanceof AgentAuthenticationToken )
			return getSubjects( (AgentAuthenticationToken) authentication );
		if ( authentication instanceof AbstractAuthenticationToken )
			return getSubjects( (AbstractAuthenticationToken) authentication );
		if ( authentication instanceof AnonymousAuthenticationToken )
			return getSubjects( (AnonymousAuthenticationToken) authentication );

		throw new IllegalArgumentException( "The authentication token isn't supported." );
	}

	public static Map<RDFNodeEnum, Set<IRI>> getSubjects( AbstractAuthenticationToken authentication ) {
		Map<RDFNodeEnum, Set<IRI>> subjects = new HashMap<>();

		addPlatformRoles( subjects, authentication );
		addPlatformPrivileges( subjects, authentication );

		return subjects;
	}

	// TODO: The creation of this resource is somewhat expensive, cache it in some way
	public static Map<RDFNodeEnum, Set<IRI>> getSubjects( AgentAuthenticationToken authentication ) {
		Map<RDFNodeEnum, Set<IRI>> subjects = new HashMap<>();

		addAgent( subjects, authentication );

		// TODO: Add groups

		addPlatformRoles( subjects, authentication );
		addPlatformPrivileges( subjects, authentication );
		addAppRoles( subjects, authentication );

		return subjects;
	}

	public static Map<RDFNodeEnum, Set<IRI>> getSubjects( AnonymousAuthenticationToken authentication ) {
		Map<RDFNodeEnum, Set<IRI>> subjects = new HashMap<>();

		return subjects;

		// TODO:
	}

	private static void addAgent( Map<RDFNodeEnum, Set<IRI>> subjects, AgentAuthenticationToken authentication ) {
		Set<IRI> agentIRIs = new HashSet<>();
		agentIRIs.add( authentication.getAgent().getIRI() );
		subjects.put( AgentDescription.Resource.CLASS, agentIRIs );
	}

	private static void addPlatformRoles( Map<RDFNodeEnum, Set<IRI>> subjects, AbstractAuthenticationToken authentication ) {
		Set<IRI> platformRoleIRIs = RDFNodeUtil.getAllIRIs( authentication.getPlatformRoles() );
		if ( platformRoleIRIs.isEmpty() ) return;
		subjects.put( PlatformRoleDescription.Resource.CLASS, platformRoleIRIs );
	}

	private static void addPlatformPrivileges( Map<RDFNodeEnum, Set<IRI>> subjects, AbstractAuthenticationToken authentication ) {
		Set<IRI> platformPrivilegeIRIs = RDFNodeUtil.getAllIRIs( authentication.getPlatformPrivileges() );
		if ( platformPrivilegeIRIs.isEmpty() ) return;
		subjects.put( PlatformPrivilegeDescription.Resource.CLASS, platformPrivilegeIRIs );
	}

	private static void addAppRoles( Map<RDFNodeEnum, Set<IRI>> subjects, AgentAuthenticationToken authentication ) {

		Set<IRI> appRoleIRIs = getIRIs( authentication.getAppRoles() );
		if ( appRoleIRIs.isEmpty() ) return;
		subjects.put( AppRoleDescription.Resource.CLASS, appRoleIRIs );
	}

	private static Set<IRI> getIRIs( Collection<? extends RDFResource> resources ) {
		return resources
			.stream()
			.map( RDFResource::getIRI )
			.collect( Collectors.toCollection( HashSet::new ) )
			;
	}
}
