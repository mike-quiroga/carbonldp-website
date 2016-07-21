package com.carbonldp.web.converters;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.utils.MediaTypeUtil;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class EmptyResponseMessageConverter implements HttpMessageConverter<EmptyResponse> {

	private List<RDFFormat> supportedFormats = Collections.emptyList();
	private List<MediaType> supportedMediaTypes = Collections.emptyList();
	private Map<MediaType, RDFFormat> mediaTypeFormats;

	// The languages are added in order of preferance. The default language goes first.
	public EmptyResponseMessageConverter() {
		//@formatter:off
		setSupportedFormats(
				Arrays.asList(
						RDFFormat.TURTLE,
						RDFFormat.JSONLD,
						RDFFormat.RDFJSON,
						RDFFormat.RDFXML
				)
		);
		//@formatter:on
	}

	private boolean supports( Class<?> clazz ) {
		return EmptyResponse.class.isAssignableFrom( clazz );
	}

	@Override
	public boolean canRead( Class<?> clazz, MediaType mediaType ) {
		return false;
	}

	@Override
	public boolean canWrite( Class<?> clazz, MediaType mediaType ) {
		return supports( clazz ) && canWrite( mediaType );
	}

	private boolean canWrite( MediaType mediaType ) {
		if ( mediaType == null || MediaType.ALL.equals( mediaType ) ) return true;

		for ( MediaType supportedMediaType : getSupportedMediaTypes() ) {
			if ( supportedMediaType.isCompatibleWith( mediaType ) ) return true;
		}

		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

	@Override
	public EmptyResponse read( Class<? extends EmptyResponse> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write( EmptyResponse response, MediaType contentType, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();

		if ( headers.getContentType() == null ) {
			if ( contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype() ) {
				contentType = MediaTypeUtil.fromString( this.getDefaultFormat().getDefaultMIMEType() );
			}
			if ( contentType != null ) {
				headers.setContentType( contentType );
			}
		}

		RDFFormat formatToUse = this.mediaTypeFormats.get( contentType );

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			writeEmptyResponse( response, formatToUse, outputStream );
		} catch ( IOException e ) {
			throw new HttpMessageNotWritableException( "The empty response couldn't be wrote to an RDF document.", e );
		}

		setContentLength( outputMessage, outputStream );

		outputStream.writeTo( outputMessage.getBody() );

		outputMessage.getBody().flush();
	}

	private void setContentLength( HttpOutputMessage outputMessage, ByteArrayOutputStream outputStream ) {
		outputMessage.getHeaders().add( HTTPHeaders.CONTENT_LENGTH, String.valueOf( outputStream.size() ) );
	}

	private void writeEmptyResponse( EmptyResponse response, RDFFormat format, OutputStream outputStream ) throws IOException {
		String emptyResponseString = null;

		if ( format.equals( RDFFormat.JSONLD ) ) {
			emptyResponseString = "{}";
		} else {
			emptyResponseString = "";
		}

		outputStream.write( emptyResponseString.getBytes() );
	}

	private void setSupportedFormats( List<RDFFormat> supportedFormats ) {
		Assert.notEmpty( supportedFormats, "'supportedFormats' must not be empty" );

		this.supportedMediaTypes = new ArrayList<>();
		this.mediaTypeFormats = new HashMap<>();

		for ( RDFFormat format : supportedFormats ) {
			List<MediaType> mediaTypes = MediaTypeUtil.fromStrings( format.getMIMETypes() );
			for ( MediaType mediaType : mediaTypes ) {
				this.mediaTypeFormats.put( mediaType, format );
			}
			this.supportedMediaTypes.addAll( mediaTypes );
		}

		this.supportedFormats = supportedFormats;
	}

	private RDFFormat getDefaultFormat() {
		return ( ! this.supportedFormats.isEmpty() ? this.supportedFormats.get( 0 ) : null );
	}

}
