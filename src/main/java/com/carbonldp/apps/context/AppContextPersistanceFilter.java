package com.carbonldp.apps.context;

import static com.carbonldp.commons.Consts.EMPTY_STRING;
import static com.carbonldp.commons.Consts.SLASH;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import com.carbonldp.PropertiesFileConfigurationRepository;
import com.carbonldp.apps.Application;

public class AppContextPersistanceFilter extends GenericFilterBean {

	static final String FILTER_APPLIED = "__carbon_acpf_applied";

	private final AppContextRepository repository;

	@Autowired
	private PropertiesFileConfigurationRepository configurationService;

	public AppContextPersistanceFilter(AppContextRepository repository) {
		this.repository = repository;
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

		URI applicationURI = getTargetApplicationURI(httpRequest, httpResponse);

		if ( applicationURI == null ) {
			// The URI doesn't match an ApplicationURI
			// TODO: Handle it
		}

		Application application = repository.getApplication(applicationURI);

		if ( application == null ) {
			// Application not found
			// TODO: Handle application not found
		}

		AppContext context = AppContextHolder.createEmptyContext();
		context.setApplication(application);

		try {
			chain.doFilter(request, response);
		} finally {
			AppContextHolder.clearContext();
			request.removeAttribute(FILTER_APPLIED);

			if ( logger.isDebugEnabled() ) {
				logger.debug("ApplicationContext cleared");
			}
		}
	}

	private URI getTargetApplicationURI(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		String requestURI = httpRequest.getRequestURI();
		requestURI = requestURI.startsWith(SLASH) ? requestURI.substring(1) : requestURI;

		if ( ! requestURI.startsWith(configurationService.getApplicationsEntryPoint()) ) return null;

		requestURI = requestURI.replace(configurationService.getApplicationsEntryPoint(), EMPTY_STRING);

		if ( requestURI.isEmpty() ) return null;

		String applicationSlug;
		int slashIndex = requestURI.indexOf(SLASH);
		if ( slashIndex == - 1 ) applicationSlug = requestURI;
		else applicationSlug = requestURI.substring(0, slashIndex);

		StringBuilder uriBuilder = new StringBuilder();
		// TODO: Decide. Should we enforce the ending slash here?
		uriBuilder.append(configurationService.getApplicationsEntryPointURL()).append(applicationSlug).append(SLASH);
		return new URIImpl(uriBuilder.toString());
	}

}
