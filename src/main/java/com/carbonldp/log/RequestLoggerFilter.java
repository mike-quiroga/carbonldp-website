package com.carbonldp.log;

import com.carbonldp.Consts;
import com.carbonldp.utils.ExceptionUtil;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.web.converters.ModelMessageConverter;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.ObjectArrayMessage;
import org.openrdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class RequestLoggerFilter extends GenericFilterBean {

	public static final String DEFAULT_FILTER_NAME = "REQUEST_LOGGER_FILTER";

	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	protected final Marker FATAL = MarkerFactory.getMarker( Consts.FATAL );
	protected ModelMessageConverter<Model> messageConverter;

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
		HttpInputMessage inputMessage = (HttpInputMessage) request.getInputStream();
		HttpOutputMessage outputMessage = (HttpOutputMessage) response.getOutputStream();
		setRequestUniqueID();

		try {
			LOG.debug( HTTPUtil.printRequestInfo( request ) );
		} finally {
			try {
				chain.doFilter( request, response );
			} catch ( Exception e ) {
				ResponseEntity<Object> responseEntity = ExceptionUtil.handleUnexpectedException( e );
				MediaType requestMediaType = inputMessage.getHeaders().getContentType();

				response.setStatus( HttpStatus.SC_INTERNAL_SERVER_ERROR );
				messageConverter.write( (Model) responseEntity.getBody(), requestMediaType, outputMessage );
				LOG.error( FATAL, "something happened. Exception: {}", e );
			} catch ( Throwable e ) {
				LOG.error( FATAL, "An exception reached the top of the chain. Exception: {}", e );
			} finally {
				//	LOG.debug( HTTPUtil.printResponseInfo( response ) );

				removeRequestUniqueID();
			}
		}
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

	@Autowired
	public void setMessageConverter( ModelMessageConverter<Model> messageConverter ) {
		this.messageConverter = messageConverter;
	}
}
