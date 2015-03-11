package com.carbonldp.authorization.acl;

import com.carbonldp.agents.AgentDescription;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AbstractAuthenticationToken;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.PlatformPrivilegeDescription;
import com.carbonldp.authorization.PlatformRoleDescription;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

public final class SubjectsRetrievalStrategy {
	private SubjectsRetrievalStrategy() {
		// Meaning non-instantiable
	}

	public static final Map<RDFNodeEnum, Set<URI>> getSubjects(Authentication authentication) {

		if ( authentication instanceof AgentAuthenticationToken )
			return getSubjects( (AgentAuthenticationToken) authentication );
		if ( authentication instanceof AbstractAuthenticationToken )
			return getSubjects( (AbstractAuthenticationToken) authentication );

		throw new IllegalArgumentException( "The authentication token isn't supported." );
	}

	public static final Map<RDFNodeEnum, Set<URI>> getSubjects(AbstractAuthenticationToken authentication) {
		Map<RDFNodeEnum, Set<URI>> subjects = new HashMap<RDFNodeEnum, Set<URI>>();

		addPlatformRoles( subjects, authentication );
		addPlatformPrivileges( subjects, authentication );

		return subjects;
	}

	// TODO: The creation of this resource is somewhat expensive, cache it in some way
	public static final Map<RDFNodeEnum, Set<URI>> getSubjects(AgentAuthenticationToken authentication) {
		Map<RDFNodeEnum, Set<URI>> subjects = new HashMap<RDFNodeEnum, Set<URI>>();

		addAgent( subjects, authentication );

		// TODO: Add groups

		addPlatformRoles( subjects, authentication );
		addPlatformPrivileges( subjects, authentication );
		addAppRoles( subjects, authentication );

		return subjects;
	}

	private static final void addAgent(Map<RDFNodeEnum, Set<URI>> subjects, AgentAuthenticationToken authentication) {
		Set<URI> agentURIs = new HashSet<URI>();
		agentURIs.add( authentication.getAgent().getURI() );
		subjects.put( AgentDescription.Resource.CLASS, agentURIs );
	}

	private static final void addPlatformRoles(Map<RDFNodeEnum, Set<URI>> subjects, AbstractAuthenticationToken authentication) {
		Set<URI> platformRoleURIs = RDFNodeUtil.getAllURIs( authentication.getPlatformRoles() );
		if ( platformRoleURIs.isEmpty() ) return;
		subjects.put( PlatformRoleDescription.Resource.CLASS, platformRoleURIs );
	}

	private static final void addPlatformPrivileges(Map<RDFNodeEnum, Set<URI>> subjects, AbstractAuthenticationToken authentication) {
		Set<URI> platformPriviligeURIs = RDFNodeUtil.getAllURIs( authentication.getPlatformPrivileges() );
		if ( platformPriviligeURIs.isEmpty() ) return;
		subjects.put( PlatformPrivilegeDescription.Resource.CLASS, platformPriviligeURIs );
	}

	private static final void addAppRoles(Map<RDFNodeEnum, Set<URI>> subjects, AgentAuthenticationToken authentication) {
		if ( AppContextHolder.getContext().isEmpty() ) return;

		URI appURI = AppContextHolder.getContext().getApplication().getURI();
		if ( !authentication.getAppsRoles().containsKey( appURI ) ) return;

		Set<URI> appRoleURIs = getURIs( authentication.getAppsRoles().get( appURI ) );
		if ( appRoleURIs.isEmpty() ) return;
		subjects.put( AppRoleDescription.Resource.CLASS, appRoleURIs );
	}

	private static final Set<URI> getURIs(Collection<? extends RDFResource> resources) {
		//@formatter:off
		return resources.stream()
						.map( RDFResource::getURI )
						.collect( Collectors.toCollection( HashSet::new ) )
				;
		//@formatter:on
	}
}
