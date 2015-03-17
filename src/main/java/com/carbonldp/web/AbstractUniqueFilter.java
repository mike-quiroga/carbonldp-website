package com.carbonldp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractUniqueFilter extends GenericFilterBean {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private final String filterAppliedFlag;

	public AbstractUniqueFilter( String filterAppliedFlag ) {
		this.filterAppliedFlag = filterAppliedFlag;
	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if ( request.getAttribute( filterAppliedFlag ) != null ) {
			// Ensure that filter is only applied once per request
			chain.doFilter( request, response );
			return;
		}

		request.setAttribute( filterAppliedFlag, Boolean.TRUE );

		try {
			applyFilter( httpRequest, httpResponse );
			chain.doFilter( request, response );
		} finally {
			try {
				cleanFilter();
			} finally {
				request.removeAttribute( filterAppliedFlag );
			}
		}
	}

	protected abstract void applyFilter( HttpServletRequest request, HttpServletResponse response );

	protected void cleanFilter() {
		// Default method. Expected to be overridden by subclasses
	}
}
