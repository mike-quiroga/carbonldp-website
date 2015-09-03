package com.carbonldp.test.web;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.converters.RDFDocumentMessageConverter;
import org.mockito.Mockito;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class RDFDocumentMessageConverterUT {
	protected String genericRequestURIString = "http://example.org/";
	protected ConfigurationRepository configurationRepository;

	@BeforeClass
	public void setUp() {
		this.configurationRepository = Mockito.mock( ConfigurationRepository.class );
		Mockito.when( this.configurationRepository.forgeGenericRequestURL() ).thenReturn( genericRequestURIString );
	}

	@Test
	public void supports__NotSupportedClass__False() {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );
		assertFalse( ReflectionTestUtils.invokeMethod( messageConverter, "supports", String.class ) );
	}

	@Test
	public void supports__SupportedClass__True() {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );
		assertTrue( ReflectionTestUtils.invokeMethod( messageConverter, "supports", RDFDocument.class ) );
	}

	@Test
	public void read__AbsoluteURIs_NoNamedGraphs_URIsBelongToASingleDocument__InferContextFromDocumentResource() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/>" +
			"	a <http://example.org/ns#DocumentResource>;" +
			"	<http://example.org/ns#fragment> <http://example.org/some-resource/#fragment-1>;" +
			"	<http://example.org/ns#bnode> [" +
			"		a <http://example.org/ns#BlankNode>" +
			"	]." +
			"<http://example.org/some-resource/#fragment-1>" +
			"	a <http://example.org/ns#FragmentResource>.";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		URI expectedDocumentURI = new URIImpl( "http://example.org/some-resource/" );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 3 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentURI );
	}

	@Test( expectedExceptions = {HttpMessageNotReadableException.class} )
	public void read__AbsoluteURIs_NoNamedGraphs_URIsBelongToMultipleDocuments__ThrowException() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/>" +
			"	a <http://example.org/ns#DocumentResource>;" +
			"	<http://example.org/ns#fragment> <http://example.org/some-resource/#fragment-1>;" +
			"	<http://example.org/ns#bnode> [" +
			"		a <http://example.org/ns#BlankNode>" +
			"	]." +
			"<http://example.org/some-resource/#fragment-1>" +
			"	a <http://example.org/ns#FragmentResource>." +
			"<http://example.org/some-other-resource/>" +
			"	a <http://example.org/ns#DocumentResource>;" +
			"	<http://example.org/ns#fragment> <http://example.org/some-other-resource/#fragment-1>;" +
			"	<http://example.org/ns#bnode> [" +
			"		a <http://example.org/ns#BlankNode>" +
			"	]." +
			"<http://example.org/some-other-resource/#fragment-1>" +
			"	a <http://example.org/ns#FragmentResource>.";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		messageConverter.read( RDFDocument.class, inputMessage );
	}

	@Test
	public void read__AbsoluteURIs_SingleNamedGraph_URIsBelongToMultipleDocuments__ReturnRDFDocumentWithNamedGraphURI() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/> {" +
			"	<http://example.org/some-resource/>" +
			"		a <http://example.org/ns#DocumentResource>;" +
			"		<http://example.org/ns#fragment> <http://example.org/some-resource/#fragment-1>;" +
			"		<http://example.org/ns#bnode> [" +
			"			a <http://example.org/ns#BlankNode>" +
			"		]." +
			"	<http://example.org/some-resource/#fragment-1>" +
			"		a <http://example.org/ns#FragmentResource>." +
			"	<http://example.org/some-other-resource/>" +
			"		a <http://example.org/ns#DocumentResource>;" +
			"		<http://example.org/ns#fragment> <http://example.org/some-other-resource/#fragment-1>;" +
			"		<http://example.org/ns#bnode> [" +
			"			a <http://example.org/ns#BlankNode>" +
			"		]." +
			"	<http://example.org/some-other-resource/#fragment-1>" +
			"		a <http://example.org/ns#FragmentResource>." +
			"}";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		URI expectedDocumentURI = new URIImpl( "http://example.org/some-resource/" );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 4, "Only resources that belong to the RDFDocument should be visible." );
		assertEquals( document.getBaseModel().subjects().size(), 6, "Resources outside of the document should be preserved, but hidden." );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentURI );
	}

	@Test( expectedExceptions = {HttpMessageNotReadableException.class} )
	public void read__AbsoluteURIs_MultipleNamedGraphs_URIsBelongToASingleDocument__ThrowException() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/> {" +
			"	<http://example.org/some-resource/>" +
			"		a <http://example.org/ns#DocumentResource>;" +
			"		<http://example.org/ns#fragment> <http://example.org/some-resource/#fragment-1>;" +
			"		<http://example.org/ns#bnode> [" +
			"			a <http://example.org/ns#BlankNode>" +
			"		]." +
			"	<http://example.org/some-resource/#fragment-1>" +
			"		a <http://example.org/ns#FragmentResource>." +
			"}" +
			"<http://example.org/some-other-resource/> {" +
			"	<http://example.org/some-other-resource/>" +
			"		a <http://example.org/ns#DocumentResource>;" +
			"		<http://example.org/ns#fragment> <http://example.org/some-other-resource/#fragment-1>;" +
			"		<http://example.org/ns#bnode> [" +
			"			a <http://example.org/ns#BlankNode>" +
			"		]." +
			"	<http://example.org/some-other-resource/#fragment-1>" +
			"		a <http://example.org/ns#FragmentResource>." +
			"}";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		messageConverter.read( RDFDocument.class, inputMessage );
	}

	@Test( expectedExceptions = {HttpMessageNotReadableException.class} )
	public void read__AbsoluteURIs_SingleNamedGraph_GeneralGraph__ThrowException() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/> {" +
			"	<http://example.org/some-resource/>" +
			"		a <http://example.org/ns#DocumentResource>;" +
			"		<http://example.org/ns#fragment> <http://example.org/some-resource/#fragment-1>;" +
			"		<http://example.org/ns#bnode> [" +
			"			a <http://example.org/ns#BlankNode>" +
			"		]." +
			"	<http://example.org/some-resource/#fragment-1>" +
			"		a <http://example.org/ns#FragmentResource>." +
			"}" +
			"<http://example.org/some-resource/>" +
			"	a <http://example.org/ns#MultiContextResource>.";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		messageConverter.read( RDFDocument.class, inputMessage );
	}

	@Test
	public void read__NullURI_NoNamedGraphs_URIsBelongToASingleDocument__ResolveURIsUsingGenericRequestURI() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<>" +
			"	a <http://example.org/ns#DocumentResource>;" +
			"	<http://example.org/ns#fragment> <#fragment-1>;" +
			"	<http://example.org/ns#bnode> [" +
			"		a <http://example.org/ns#BlankNode>" +
			"	]." +
			"<#fragment-1>" +
			"	a <http://example.org/ns#FragmentResource>.";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		URI expectedDocumentURI = new URIImpl( this.configurationRepository.forgeGenericRequestURL() );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 3 );
		assertEquals( document.getFragmentResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentURI );
	}

	@Test
	public void read__NullURI_SingleNamedGraph_URIsBelongToASingleDocument__ResolveURIsUsingGenericRequestURI() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<> {" +
			"	<>" +
			"		a <http://example.org/ns#DocumentResource>;" +
			"		<http://example.org/ns#fragment> <#fragment-1>;" +
			"		<http://example.org/ns#bnode> [" +
			"			a <http://example.org/ns#BlankNode>" +
			"		]." +
			"	<#fragment-1>" +
			"		a <http://example.org/ns#FragmentResource>." +
			"}";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		URI expectedDocumentURI = new URIImpl( this.configurationRepository.forgeGenericRequestURL() );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 3 );
		assertEquals( document.getFragmentResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentURI );
	}

	@Test( expectedExceptions = {HttpMessageNotReadableException.class} )
	public void read__OnlyBlankNodes_NoNamedGraphs__ThrowException() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"[" +
			"	a <http://example.org/ns#BlankNode>" +
			"].";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		messageConverter.read( RDFDocument.class, inputMessage );
	}

	@Test
	public void read__OnlyBlankNodes_InNamedGraph__ReturnRDFDocumentWithNamedGraphURI() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/> {" +
			"	[" +
			"		a <http://example.org/ns#BlankNode>" +
			"	]." +
			"}";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		URI expectedDocumentURI = new URIImpl( "http://example.org/some-resource/" );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentURI );
	}

	private HttpInputMessage prepareHTTPInputMessage( String body, RDFFormat rdfFormat ) throws IOException {
		HttpInputMessage inputMessage = Mockito.mock( HttpInputMessage.class );

		InputStream stream = new ByteArrayInputStream( body.getBytes( StandardCharsets.UTF_8 ) );
		Mockito.when( inputMessage.getBody() ).thenReturn( stream );

		HttpHeaders httpHeaders = Mockito.mock( HttpHeaders.class );
		Mockito.when( httpHeaders.getContentType() ).thenReturn( MediaType.parseMediaType( rdfFormat.getDefaultMIMEType() ) );
		Mockito.when( inputMessage.getHeaders() ).thenReturn( httpHeaders );

		return inputMessage;
	}
}
