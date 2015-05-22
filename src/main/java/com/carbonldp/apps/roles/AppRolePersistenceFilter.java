package com.carbonldp.apps.roles;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.web.AbstractUniqueFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class AppRolePersistenceFilter extends AbstractUniqueFilter {

	private final AppRoleRepository appRoleRepository;
	public static final String FILTER_APPLIED = "__carbon_arpf_applied";

	public AppRolePersistenceFilter( AppRoleRepository appRoleRepository ) {
		super( FILTER_APPLIED );
		this.appRoleRepository = appRoleRepository;
	}

	@Override
	protected void applyFilter( HttpServletRequest request, HttpServletResponse response ) {
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) throw new IllegalStateException( "The filter needs to execute inside of an appContext." );

		Authentication rawAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if ( rawAuthentication == null ) return;

		if ( ! ( rawAuthentication instanceof AppRolesHolder ) ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "The rawAuthentication token doesn't support appRoles." );
			}
			return;
		}

		App app = appContext.getApplication();
		AppRolesHolder appRolesHolder = (AppRolesHolder) rawAuthentication;

		if ( rawAuthentication instanceof AgentAuthenticationToken ) {
			Agent agent = ( (AgentAuthenticationToken) rawAuthentication ).getAgent();
			Set<AppRole> appRoles = appRoleRepository.get( agent );
			appRolesHolder.setAppRoles( app.getURI(), appRoles );
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "The authentication token isn't supported (yet)." );
			}
		}
	}
}
