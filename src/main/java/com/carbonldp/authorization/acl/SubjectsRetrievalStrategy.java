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
import org.openrdf.model.URI;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

public final class SubjectsRetrievalStrategy {
	private SubjectsRetrievalStrategy() {
		// Meaning non-instantiable
	}

	public static Map<RDFNodeEnum, Set<URI>> getSubjects( Authentication authentication ) {

		if ( authentication instanceof AgentAuthenticationToken )
			return getSubjects( (AgentAuthenticationToken) authentication );
		if ( authentication instanceof AbstractAuthenticationToken )
			return getSubjects( (AbstractAuthenticationToken) authentication );
		if ( authentication instanceof AnonymousAuthenticationToken )
			return getSubjects( (AnonymousAuthenticationToken) authentication );

		throw new IllegalArgumentException( "The authentication token isn't supported." );
	}

	public static Map<RDFNodeEnum, Set<URI>> getSubjects( AbstractAuthenticationToken authentication ) {
		Map<RDFNodeEnum, Set<URI>> subjects = new HashMap<>();

		addPlatformRoles( subjects, authentication );
		addPlatformPrivileges( subjects, authentication );

		return subjects;
	}

	// TODO: The creation of this resource is somewhat expensive, cache it in some way
	public static Map<RDFNodeEnum, Set<URI>> getSubjects( AgentAuthenticationToken authentication ) {
		Map<RDFNodeEnum, Set<URI>> subjects = new HashMap<>();

		addAgent( subjects, authentication );

		// TODO: Add groups

		addPlatformRoles( subjects, authentication );
		addPlatformPrivileges( subjects, authentication );
		addAppRoles( subjects, authentication );

		return subjects;
	}

	public static Map<RDFNodeEnum, Set<URI>> getSubjects( AnonymousAuthenticationToken authentication ) {
		Map<RDFNodeEnum, Set<URI>> subjects = new HashMap<>();

		return subjects;

		// TODO:
	}

	private static void addAgent( Map<RDFNodeEnum, Set<URI>> subjects, AgentAuthenticationToken authentication ) {
		Set<URI> agentURIs = new HashSet<>();
		agentURIs.add( authentication.getAgent().getIRI() );
		subjects.put( AgentDescription.Resource.CLASS, agentURIs );
	}

	private static void addPlatformRoles( Map<RDFNodeEnum, Set<URI>> subjects, AbstractAuthenticationToken authentication ) {
		Set<URI> platformRoleURIs = RDFNodeUtil.getAllIRIs( authentication.getPlatformRoles() );
		if ( platformRoleURIs.isEmpty() ) return;
		subjects.put( PlatformRoleDescription.Resource.CLASS, platformRoleURIs );
	}

	private static void addPlatformPrivileges( Map<RDFNodeEnum, Set<URI>> subjects, AbstractAuthenticationToken authentication ) {
		Set<URI> platformPrivilegeURIs = RDFNodeUtil.getAllIRIs( authentication.getPlatformPrivileges() );
		if ( platformPrivilegeURIs.isEmpty() ) return;
		subjects.put( PlatformPrivilegeDescription.Resource.CLASS, platformPrivilegeURIs );
	}

	private static void addAppRoles( Map<RDFNodeEnum, Set<URI>> subjects, AgentAuthenticationToken authentication ) {

		Set<URI> appRoleURIs = getURIs( authentication.getAppRoles() );
		if ( appRoleURIs.isEmpty() ) return;
		subjects.put( AppRoleDescription.Resource.CLASS, appRoleURIs );
	}

	private static Set<URI> getURIs( Collection<? extends RDFResource> resources ) {
		return resources
			.stream()
			.map( RDFResource::getIRI )
			.collect( Collectors.toCollection( HashSet::new ) )
			;
	}
}
