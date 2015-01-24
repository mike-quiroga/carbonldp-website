package com.carbonldp.apps.context;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.openrdf.model.URI;
import org.springframework.web.filter.GenericFilterBean;

import com.carbonldp.apps.Application;

public class ApplicationContextPersistanceFilter extends GenericFilterBean {

	static final String FILTER_APPLIED = "__carbon_acpf_applied";

	private final ApplicationContextRepository repository;

	public ApplicationContextPersistanceFilter(ApplicationContextRepository repository) {
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

		URI applicationURI = null;
		// TODO: Retrieve application URI from request URL

		Application application = repository.getApplication(applicationURI);

		if ( application == null ) {
			// Application not found
			// TODO: Handle application not found
		}

		ApplicationContext context = ApplicationContextHolder.createEmptyContext();
		context.setApplication(application);

		try {
			chain.doFilter(request, response);
		} finally {
			ApplicationContextHolder.clearContext();
			request.removeAttribute(FILTER_APPLIED);

			if ( logger.isDebugEnabled() ) {
				logger.debug("ApplicationContext cleared");
			}
		}
	}

}
