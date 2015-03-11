package com.carbonldp.apps.context;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import com.carbonldp.PropertiesFileConfigurationRepository;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;

public class AppContextPersistanceFilter extends GenericFilterBean {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	static final String FILTER_APPLIED = "__carbon_acpf_applied";

	private final AppContextRepository appContextRepository;

	@Autowired
	private PropertiesFileConfigurationRepository configurationService;

	public AppContextPersistanceFilter(AppContextRepository appContextRepository) {
		this.appContextRepository = appContextRepository;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if ( request.getAttribute(FILTER_APPLIED) != null ) {
			// Ensure that filter is only applied once per request
			chain.doFilter(request, response);
			return;
		}

		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		URI rootContainerURI = getRootContainerURI(httpRequest, httpResponse);

		if ( rootContainerURI == null ) {
			// The URI doesn't match an App's Root Container URI
			// TODO: Add more information
			request.removeAttribute(FILTER_APPLIED);
			httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}

		App app = appContextRepository.getApp(rootContainerURI);

		if ( app == null ) {
			// TODO: Add more information
			request.removeAttribute(FILTER_APPLIED);
			httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}

		AppContext context = AppContextHolder.createEmptyContext();
		context.setApplication(app);
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("AppContext set to: '{}'", app);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			AppContextHolder.clearContext();
			request.removeAttribute(FILTER_APPLIED);

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("AppContext cleared");
			}
		}
	}

	private URI getRootContainerURI(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		String requestURI = httpRequest.getRequestURI();
		requestURI = requestURI.startsWith(SLASH) ? requestURI.substring(1) : requestURI;

		if ( ! requestURI.startsWith(Vars.getAppsEntryPoint()) ) return null;

		requestURI = requestURI.replace(Vars.getAppsEntryPoint(), EMPTY_STRING);

		if ( requestURI.isEmpty() ) return null;

		String applicationSlug;
		int slashIndex = requestURI.indexOf(SLASH);
		if ( slashIndex == - 1 ) applicationSlug = requestURI;
		else applicationSlug = requestURI.substring(0, slashIndex);

		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(Vars.getAppsEntryPointURL()).append(applicationSlug).append(SLASH);
		return new URIImpl(uriBuilder.toString());
	}

}
