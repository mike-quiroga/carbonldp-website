package com.carbonldp.ldp.nonrdf;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.ldp.web.AbstractGETRequestHandler;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.rdf.RDFResourceDescription;
import com.carbonldp.utils.HTTPUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class NonRDFSourceMessageConverter implements HttpMessageConverter<AbstractGETRequestHandler.RDFRepresentationFileWrapper> {

	protected boolean supports( Class<?> clazz ) {
		return AbstractGETRequestHandler.RDFRepresentationFileWrapper.class.isAssignableFrom( clazz );
	}

	@Override
	public void write( AbstractGETRequestHandler.RDFRepresentationFileWrapper wrapper, MediaType mediaType, HttpOutputMessage httpOutputMessage ) throws IOException, HttpMessageNotWritableException {
		RDFRepresentation rdfRepresentation = wrapper.getRdfRepresentation();
		File file = wrapper.getFile();


		// TODO: Check that the requested mediaType matches the stored one (M)


		HttpHeaders headers = httpOutputMessage.getHeaders();

		addLocationHeader( headers, rdfRepresentation );
		addContentTypeHeader( headers, rdfRepresentation );
		addLinkTypeHeaders( headers );
		addETagHeader( headers, rdfRepresentation );
		addDescribedByHeader( headers, rdfRepresentation );
		addContentLength( headers, rdfRepresentation );

		writeFile( file, httpOutputMessage );
	}

	private void addContentLength( HttpHeaders headers, RDFRepresentation rdfRepresentation ) {
		headers.add( HTTPHeaders.CONTENT_LENGTH, String.valueOf( rdfRepresentation.getSize() ) );
	}

	private void addDescribedByHeader( HttpHeaders headers, RDFRepresentation rdfRepresentation ) {
		String describedBy = "<" + rdfRepresentation.getURI() + ">";

		HTTPHeaderValue headerValue = new HTTPHeaderValue();
		headerValue.setMainValue( describedBy );
		headerValue.setExtendingKey( "rel" );
		headerValue.setExtendingValue( "describedby" );

		headers.add( HTTPHeaders.LINK, headerValue.toString() );
	}

	private void addETagHeader( HttpHeaders headers, RDFRepresentation rdfRepresentation ) {
		if ( rdfRepresentation.getModified() != null ) {
			headers.add( HTTPHeaders.ETAG, HTTPUtil.formatWeakETag( rdfRepresentation.getModified().toString() ) );
		}
	}

	private void addContentTypeHeader( HttpHeaders headers, RDFRepresentation rdfRepresentation ) {
		headers.add( HTTPHeaders.CONTENT_TYPE, rdfRepresentation.getMediaType() );
	}

	private void addLocationHeader( HttpHeaders headers, RDFRepresentation rdfRepresentation ) {
		headers.add( HTTPHeaders.LOCATION, rdfRepresentation.getURI().stringValue() );
	}

	private void addLinkTypeHeaders( HttpHeaders headers ) {
		HTTPHeaderValue header = new HTTPHeaderValue();

		header.setMainValue( RDFResourceDescription.Resource.CLASS.getURI().stringValue() );
		header.setExtendingKey( "rel" );
		header.setExtendingValue( "type" );

		headers.add( HTTPHeaders.LINK, header.toString() );

		header.setMainValue( RDFRepresentationDescription.Resource.NON_RDF_SOURCE.getURI().stringValue() );

		headers.add( HTTPHeaders.LINK, header.toString() );
	}

	private void writeFile( File file, HttpOutputMessage httpOutputMessage ) {
		try {
			OutputStream bodyOutputStream = httpOutputMessage.getBody();
			Files.copy( file.toPath(), bodyOutputStream );
			bodyOutputStream.flush();
		} catch ( IOException e ) {
			throw new RuntimeException( "File couldn't be loaded, nested Exception:", e );
		}
	}

	@Override
	public boolean canRead( java.lang.Class clazz, MediaType mediaType ) {
		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList( MediaType.ALL );
	}

	@Override
	public AbstractGETRequestHandler.RDFRepresentationFileWrapper read( Class clazz, HttpInputMessage httpInputMessage ) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	public boolean canWrite( Class clazz, MediaType mediaType ) {
		return supports( clazz );
	}
}
