package com.carbonldp.log;

import com.carbonldp.Consts;
import com.carbonldp.utils.HTTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestLoggerFilter extends GenericFilterBean {

	public static final String DEFAULT_FILTER_NAME = "REQUEST_LOGGER_FILTER";

	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	protected final Marker FATAL = MarkerFactory.getMarker( Consts.FATAL );

	@Override
	public void doFilter( ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain ) throws IOException, ServletException {
		if ( ! LOG.isDebugEnabled() ) {
			chain.doFilter( rawRequest, rawResponse );
			return;
		}

		if ( ! ( rawRequest instanceof HttpServletRequest ) || ! ( rawResponse instanceof HttpServletResponse ) ) {
			LOG.debug( "Request and/or response are not HTTP" );

			chain.doFilter( rawRequest, rawResponse );
			return;
		}

		HttpServletRequest request = (HttpServletRequest) rawRequest;
		HttpServletResponse response = (HttpServletResponse) rawResponse;

		try {
			LOG.debug( HTTPUtil.printRequestInfo( request ) );
		} finally {
			try {
				chain.doFilter( request, response );
			} catch ( Throwable e ) {
				LOG.error( FATAL, "An exception reached the top of the chain. Exception: {}", e );
			} finally {
				LOG.debug( HTTPUtil.printResponseInfo( response ) );
			}
		}
	}
}
