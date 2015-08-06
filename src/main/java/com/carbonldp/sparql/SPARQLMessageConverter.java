package com.carbonldp.sparql;

import com.carbonldp.utils.MediaTypeUtil;
import info.aduna.lang.FileFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SPARQLMessageConverter<E extends SPARQLResult, F extends FileFormat> implements HttpMessageConverter<E> {
	protected List<F> supportedFormats;
	protected List<MediaType> supportedMediaTypes;
	protected HashMap<MediaType, F> mediaTypeFormats;

	@Override
	public boolean canRead( Class aClass, MediaType mediaType ) {
		return false;
	}

	@Override
	public boolean canWrite( Class<?> clazz, MediaType mediaType ) {
		boolean sup = supports ( clazz );
		boolean can = canWrite( mediaType);

		return sup&&can;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

	protected boolean canWrite( MediaType mediaType ) {
		if ( mediaType == null || MediaType.ALL.equals( mediaType ) ) return true;

		for ( MediaType supportedMediaType : getSupportedMediaTypes() ) {
			if ( supportedMediaType.isCompatibleWith( mediaType ) ) return true;
		}

		return false;
	}

	protected void setSupportedFormats( List<F> supportedFormats ) {
		Assert.notEmpty( supportedFormats, "'supportedFormats' must not be empty" );

		this.supportedMediaTypes = new ArrayList<>();
		this.mediaTypeFormats = new HashMap<>();

		for ( F format : supportedFormats ) {
			List<MediaType> mediaTypes = MediaTypeUtil.fromStrings( format.getMIMETypes() );
			for ( MediaType mediaType : mediaTypes ) {
				this.mediaTypeFormats.put( mediaType, format );
			}
			this.supportedMediaTypes.addAll( mediaTypes );
		}

		this.supportedFormats = supportedFormats;
	}

	@Override
	public void write( SPARQLResult sparqlResult, MediaType contentType, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException {

		HttpHeaders headers = outputMessage.getHeaders();
		setContentType( contentType, headers );
		setAdditionalHeaders( headers );
		writeSparqlResult( sparqlResult, contentType, outputMessage );
		outputMessage.getBody().flush();
	}

	protected abstract void writeSparqlResult( SPARQLResult sparqlResult, MediaType contentType, HttpOutputMessage outputMessage );

	@Override
	public E read( Class aClass, HttpInputMessage httpInputMessage ) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	protected abstract boolean supports( Class<?> clazz );

	protected void setAdditionalHeaders( HttpHeaders headers ) {
		// Should be override if needed
	}

	protected F getDefaultFormat() {
		return ( ! this.supportedFormats.isEmpty() ? this.supportedFormats.get( 0 ) : null );
	}

	protected void setContentType( MediaType contentType, HttpHeaders headers ) {
		if ( headers.getContentType() == null ) {
			if ( contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype() ) {
				contentType = MediaTypeUtil.fromString( this.getDefaultFormat().getDefaultMIMEType() );
			}
			if ( contentType != null ) {
				headers.setContentType( contentType );
			}
		}
	}
}
