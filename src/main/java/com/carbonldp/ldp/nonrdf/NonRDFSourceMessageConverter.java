package com.carbonldp.ldp.nonrdf;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.http.Link;
import com.carbonldp.ldp.containers.BasicContainerDescription;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.web.AbstractGETRequestHandler;
import com.carbonldp.rdf.RDFNodeEnum;
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
		Link link = new Link( rdfRepresentation.getIRI().stringValue() );
		link.addRelationshipType( Consts.DESCRIBED_BY );
		link.setAnchor( rdfRepresentation.getIRI().stringValue() );

		headers.add( HTTPHeaders.LINK, link.toString() );
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
		headers.add( HTTPHeaders.LOCATION, rdfRepresentation.getIRI().stringValue() );
	}

	protected String addTypeLinkHeader( RDFNodeEnum interactionModel ) {
		Link link = new Link( interactionModel.getIRI().stringValue() );
		link.addRelationshipType( Consts.TYPE );

		return link.toString();
	}

	protected String addInteractionModelLinkHeader( RDFNodeEnum interactionModel ) {
		Link link = new Link( interactionModel.getIRI().stringValue() );
		link.addRelationshipType( Consts.INTERACTION_MODEL );

		return link.toString();
	}

	private void addLinkTypeHeaders( HttpHeaders headers ) {
		headers.add( HTTPHeaders.LINK, addTypeLinkHeader( RDFResourceDescription.Resource.CLASS ) );
		headers.add( HTTPHeaders.LINK, addTypeLinkHeader( RDFRepresentationDescription.Resource.NON_RDF_SOURCE ) );
		headers.add( HTTPHeaders.LINK, addTypeLinkHeader( RDFSourceDescription.Resource.CLASS ) );
		headers.add( HTTPHeaders.LINK, addTypeLinkHeader( ContainerDescription.Resource.CLASS ) );
		headers.add( HTTPHeaders.LINK, addTypeLinkHeader( BasicContainerDescription.Resource.CLASS ) );

		headers.add( HTTPHeaders.LINK, addInteractionModelLinkHeader( RDFRepresentationDescription.Resource.NON_RDF_SOURCE ));
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
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canWrite( Class clazz, MediaType mediaType ) {
		return supports( clazz );
	}
}
