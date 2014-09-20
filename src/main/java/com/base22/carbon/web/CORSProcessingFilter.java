package com.base22.carbon.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import com.base22.carbon.utils.HTTPUtil;

/**
 * Cross-Origin Resource Sharing filter that handles the necessary stuff for cross domain Ajax calls to work.
 */
public class CORSProcessingFilter extends GenericFilterBean {

	static final Logger LOG = LoggerFactory.getLogger(CORSProcessingFilter.class);

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		LOG.debug(HTTPUtil.printRequestInfo(request));
		String method = request.getMethod();
		if ( method.equals("OPTIONS") ) {
			if ( request.getHeader("access-control-request-method") != null ) {
				if ( LOG.isTraceEnabled() ) {
					LOG.trace(">> doFilter() > CORS request intercepted.");
				}

				response.addHeader("Access-Control-Allow-Origin", "*");
				response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
				response.addHeader("Access-Control-Allow-Credentials", "true");
				response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
				response.getWriter().print("OK");
				response.getWriter().flush();

				LOG.debug(HTTPUtil.printResponseInfo(response));
				return;
			}
		}
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));

		// TODO: Create the tables needed for application-domain configuration
		// TODO: Use this application-domain configuration when intercepting the CORS request
		// TODO: Add this headers when the application context is loaded

		filterChain.doFilter(request, response);
		return;
	}
}