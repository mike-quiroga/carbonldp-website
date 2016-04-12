package com.carbonldp.web.converters;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.utils.MediaTypeUtil;
import com.carbonldp.utils.ModelUtil;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
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
import java.util.*;

public abstract class ModelMessageConverter<E extends Model> implements HttpMessageConverter<E> {
	protected List<RDFFormat> supportedFormats = Collections.emptyList();
	protected List<MediaType> supportedMediaTypes = Collections.emptyList();
	protected Map<MediaType, RDFFormat> mediaTypeFormats;

	private final boolean canRead;
	private final boolean canWrite;

	protected ModelMessageConverter( boolean canRead, boolean canWrite ) {
		setSupportedFormats(
			Arrays.asList(
				RDFFormat.TURTLE,
				RDFFormat.JSONLD,
				RDFFormat.RDFJSON,
				RDFFormat.RDFXML,
				RDFFormat.TRIG,
				RDFFormat.NTRIPLES,
				RDFFormat.N3,
				RDFFormat.TRIX,
				RDFFormat.BINARY,
				RDFFormat.NQUADS
			)
		);

		this.canRead = canRead;
		this.canWrite = canWrite;
	}

	protected abstract boolean supports( Class<?> clazz );

	@Override
	public boolean canRead( Class<?> clazz, MediaType mediaType ) {
		if ( ! canRead ) return false;
		return supports( clazz ) && canRead( mediaType );
	}

	protected boolean canRead( MediaType mediaType ) {
		if ( mediaType == null || MediaType.ALL.equals( mediaType ) ) return true;

		for ( MediaType supportedMediaType : getSupportedMediaTypes() ) {
			if ( supportedMediaType.isCompatibleWith( mediaType ) ) return true;
		}

		return false;
	}

	@Override
	public boolean canWrite( Class<?> clazz, MediaType mediaType ) {
		if ( ! canWrite ) return false;
		return supports( clazz ) && canWrite( mediaType );
	}

	protected boolean canWrite( MediaType mediaType ) {
		if ( mediaType == null || MediaType.ALL.equals( mediaType ) ) return true;

		for ( MediaType supportedMediaType : getSupportedMediaTypes() ) {
			if ( supportedMediaType.isCompatibleWith( mediaType ) ) return true;
		}

		return false;
	}

	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

	protected RDFFormat getFormatToUse( MediaType requestMediaType ) {
		for ( MediaType supportedMediaType : this.mediaTypeFormats.keySet() ) {
			if ( supportedMediaType.isCompatibleWith( requestMediaType ) ) return this.mediaTypeFormats.get( supportedMediaType );
		}
		return this.getDefaultFormat();
	}

	protected RDFFormat getDefaultFormat() {
		return ( ! this.supportedFormats.isEmpty() ? this.supportedFormats.get( 0 ) : null );
	}

	@Override
	public E read( Class<? extends E> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException {
		throw new IllegalStateException();
	}

	@Override
	public void write( E model, MediaType contentType, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException {
		if ( ! canWrite ) throw new IllegalStateException();

		HttpHeaders headers = outputMessage.getHeaders();
		setContentType( contentType, headers );
		setAdditionalHeaders( model, headers );
		Model toWrite = getModelToWrite( model );
		writeModel( toWrite, this.mediaTypeFormats.get( contentType ), outputMessage );

	}

	protected Model getModelToWrite( E model ) {
		return model;
	}

	protected void setAdditionalHeaders( E model, HttpHeaders headers ) {
		// Should be override if needed
	}

	private void setContentType( MediaType contentType, HttpHeaders headers ) {
		if ( headers.getContentType() == null ) {
			if ( contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype() ) {
				contentType = MediaTypeUtil.fromString( this.getDefaultFormat().getDefaultMIMEType() );
			}
			if ( contentType != null ) {
				headers.setContentType( contentType );
			}
		}
	}

	protected void writeModel( Model model, RDFFormat format, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		RDFWriter writer = Rio.createWriter( format, outputStream );
		try {
			writer.startRDF();
			for ( Statement statement : model ) {
				writer.handleStatement( statement );
			}
			writer.endRDF();
		} catch ( RDFHandlerException e ) {
			throw new HttpMessageNotWritableException( "The RDF model couldn't be wrote to an RDF document.", e );
		}

		setContentLength( outputMessage, outputStream );

		outputStream.writeTo( outputMessage.getBody() );
		outputMessage.getBody().flush();
	}

	private void setContentLength( HttpOutputMessage outputMessage, ByteArrayOutputStream outputStream ) {
		outputMessage.getHeaders().add( HTTPHeaders.CONTENT_LENGTH, String.valueOf( outputStream.size() ) );
	}

	protected void setSupportedFormats( List<RDFFormat> supportedFormats ) {
		Assert.notEmpty( supportedFormats, "'supportedFormats' must not be empty" );

		this.supportedMediaTypes = new ArrayList<MediaType>();
		this.mediaTypeFormats = new HashMap<MediaType, RDFFormat>();

		for ( RDFFormat format : supportedFormats ) {
			List<MediaType> mediaTypes = MediaTypeUtil.fromStrings( format.getMIMETypes() );
			for ( MediaType mediaType : mediaTypes ) {
				this.mediaTypeFormats.put( mediaType, format );
			}
			this.supportedMediaTypes.addAll( mediaTypes );
		}

		this.supportedFormats = supportedFormats;
	}

	protected void setETagHeader( Model model, HttpHeaders headers ) {
		int eTag = ModelUtil.calculateETag( model );
		String headerETag = HTTPUtil.formatStrongEtag( eTag );
		headers.set( HTTPHeaders.ETAG, headerETag );
	}
}
