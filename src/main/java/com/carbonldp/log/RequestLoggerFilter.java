package com.carbonldp.log;

import com.carbonldp.Consts;
import com.carbonldp.config.PropertiesFileConfigurationRepository;
import com.carbonldp.exceptions.ExceptionConverter;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.web.converters.AbstractModelMessageConverter;
import org.apache.logging.log4j.ThreadContext;
import org.openrdf.model.impl.AbstractModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
		setRequestUniqueID();

		try {
			LOG.debug( HTTPUtil.printRequestInfo( request ) );
		} finally {
			try {
				chain.doFilter( request, response );
			} catch ( Exception e ) {
				ResponseEntity<Object> responseEntity = ExceptionConverter.convertException( response, e );
				exceptionMessageWriter( request, response, responseEntity );
				LOG.error( "An exception reached the top of the chain. Exception: {}", e );
			} finally {
				LOG.debug( HTTPUtil.printResponseInfo( response ) );

				removeRequestUniqueID();
			}
		}
	}

	private void exceptionMessageWriter( HttpServletRequest request, HttpServletResponse response, ResponseEntity<Object> responseEntity ) throws IOException {
		HttpInputMessage inputMessage = new ServletServerHttpRequest( request );
		HttpOutputMessage outputMessage = new ServletServerHttpResponse( response );

		AbstractModelMessageConverter messageConverter = new AbstractModelMessageConverter( new PropertiesFileConfigurationRepository() );
		List<MediaType> requestMediaTypes = inputMessage.getHeaders().getAccept();
		List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
		MediaType requestMediaType = null;
		if (
			requestMediaTypes.size() == 1 &&
				( requestMediaTypes.get( 0 ).isWildcardType() || requestMediaTypes.get( 0 ).isWildcardSubtype() )
			)
			requestMediaType = supportedMediaTypes.get( 0 );
		for ( MediaType mediaType : requestMediaTypes ) {
			if ( supportedMediaTypes.contains( mediaType ) ) {
				requestMediaType = mediaType;
				break;
			}
		}
		if ( requestMediaType != null ) messageConverter.write( (AbstractModel) responseEntity.getBody(), requestMediaType, outputMessage );
	}

	private void setRequestUniqueID() {
		String requestID = UUID.randomUUID().toString();
		String shortRequestID = requestID.split( "-" )[0];

		ThreadContext.put( "requestID", requestID );
		ThreadContext.put( "shortRequestID", shortRequestID );

		// TODO: Add it to a requestContextHolder
	}

	private void removeRequestUniqueID() {
		ThreadContext.remove( "requestID" );
		ThreadContext.remove( "shortRequestID" );
		if ( ThreadContext.isEmpty() ) ThreadContext.clearMap();
	}
}
