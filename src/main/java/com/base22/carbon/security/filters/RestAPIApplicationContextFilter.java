package com.base22.carbon.security.filters;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.dao.ApplicationDAO;
import com.base22.carbon.security.models.Application;
import com.base22.carbon.security.services.PermissionService;
import com.base22.carbon.security.tokens.ApplicationContextToken;
import com.base22.carbon.security.utils.AuthenticationUtil;
import com.base22.carbon.services.ConfigurationService;

public class RestAPIApplicationContextFilter extends GenericFilterBean {

	@Autowired
	private ApplicationDAO applicationDAO;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private ConfigurationService configurationService;

	static final Logger LOG = LoggerFactory.getLogger(RestAPIApplicationContextFilter.class);

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		Authentication authentication = null;

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> doFilter() > Will try to set the application context.");
		}

		try {
			authentication = SecurityContextHolder.getContext().getAuthentication();
		} catch (Exception exception) {
			// The security context couldn't be retrieved, so the filter will not be executed
			filterChain.doFilter(request, response);
			return;
		}

		if ( authentication == null ) {
			// The authentication is empty, return
			if ( LOG.isTraceEnabled() ) {
				LOG.trace("<< doFilter() > The authentication token is not set.");
			}
			filterChain.doFilter(request, response);
			return;
		}

		if ( ! (authentication instanceof ApplicationContextToken) ) {
			// The authentication cannot hold an application context, return
			if ( LOG.isTraceEnabled() ) {
				LOG.trace("<< doFilter() > The authentication token doesn't support application context.");
			}
			filterChain.doFilter(request, response);
			return;
		}

		ApplicationContextToken token = (ApplicationContextToken) authentication;

		String applicationIdentifier = getApplicationIdentifierFromURI(request.getRequestURI());
		if ( applicationIdentifier == null ) {
			if ( LOG.isTraceEnabled() ) {
				LOG.trace("<< doFilter() > An application identifier wasn't specified.");
			}
			filterChain.doFilter(request, response);
			return;
		}
		// Check if it is not a reserved application name
		if ( configurationService.getReservedApplicationNames().contains(applicationIdentifier) ) {
			// It is reserved, return
			if ( LOG.isTraceEnabled() ) {
				LOG.trace("<< doFilter() > The application identifier is a reserved name, so the context has not been set.");
			}
			filterChain.doFilter(request, response);
			return;
		}

		Application application = null;
		try {
			application = getApplicationFromRequest(applicationIdentifier);
		} catch (Exception exception) {

		}

		if ( application != null ) {
			token.setCurrentApplicationContext(application);
			if ( LOG.isTraceEnabled() ) {
				LOG.trace("<< doFilter() > The application context has been set to hold the application: '{}'.", application.getUuidString());
			}
			filterChain.doFilter(request, response);
			return;
		} else {
			// TODO: Return an error response object
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "The application wasn't found.");
			return;
		}
	}

	private Application getApplicationFromRequest(String applicationIdentifier) {
		Application application = null;

		try {
			// Check if the identifier is the UUID of the application or the unique name
			if ( AuthenticationUtil.isUUIDString(applicationIdentifier) ) {
				// The identifier is a uuid
				UUID applicationUUID = AuthenticationUtil.restoreUUID(applicationIdentifier);
				application = applicationDAO.findByUUID(applicationUUID);
			} else {
				// The identifier is a unique name
				application = applicationDAO.findBySlug(applicationIdentifier);
			}
		} catch (CarbonException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getApplicationFromRequest() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplicationFromRequest() > The application with identifier: '{}', couldn't be loaded.", applicationIdentifier);
			}
		}

		if ( application == null ) {
			if ( LOG.isTraceEnabled() ) {
				LOG.trace("<< getApplicationFromRequest() > The application with identifier:'{}', couldn't be found.", applicationIdentifier);
			}
		}

		return application;
	}

	private String getApplicationIdentifierFromURI(String uri) {
		String applicationIdentifier = null;

		String[] uriSegments = uri.split("/");
		if ( uriSegments.length >= 3 ) {
			applicationIdentifier = uriSegments[2];
		}

		return applicationIdentifier;
	}

	public void setApplicationDAO(ApplicationDAO applicationDAO) {
		this.applicationDAO = applicationDAO;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
