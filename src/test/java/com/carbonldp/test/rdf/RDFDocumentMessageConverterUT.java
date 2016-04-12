package com.carbonldp.test.rdf;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFDocumentMessageConverter;
import com.carbonldp.web.exceptions.BadRequestException;
import org.mockito.Mockito;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.RDFFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
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
 * @since 0.10.0-ALPHA
 */
public class RDFDocumentMessageConverterUT {
	protected String genericRequestIRIString = "http://example.org/";
	protected ConfigurationRepository configurationRepository;

	@BeforeClass
	public void setUp() {
		this.configurationRepository = Mockito.mock( ConfigurationRepository.class );
		Mockito.when( this.configurationRepository.forgeGenericRequestURL() ).thenReturn( genericRequestIRIString );
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
	public void read__AbsoluteIRIs_NoNamedGraphs_IRIsBelongToASingleDocument__InferContextFromDocumentResource() throws Exception {
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

		IRI expectedDocumentIRI = SimpleValueFactory.getInstance().createIRI( "http://example.org/some-resource/" );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 3 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentIRI );
	}

	@Test( expectedExceptions = {BadRequestException.class} )
	public void read__AbsoluteIRIs_NoNamedGraphs_IRIsBelongToMultipleDocuments__ThrowException() throws Exception {
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
	public void read__AbsoluteIRIs_SingleNamedGraph_IRIsBelongToMultipleDocuments__ReturnRDFDocumentWithNamedGraphIRI() throws Exception {
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

		IRI expectedDocumentIRI = SimpleValueFactory.getInstance().createIRI( "http://example.org/some-resource/" );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 4, "Only resources that belong to the RDFDocument should be visible." );
		assertEquals( document.getBaseModel().subjects().size(), 6, "Resources outside of the document should be preserved, but hidden." );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentIRI );
	}

	@Test( expectedExceptions = {BadRequestException.class} )
	public void read__AbsoluteIRIs_MultipleNamedGraphs_IRIsBelongToASingleDocument__ThrowException() throws Exception {
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

	@Test( expectedExceptions = {BadRequestException.class} )
	public void read__AbsoluteIRIs_SingleNamedGraph_GeneralGraph__ThrowException() throws Exception {
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
	public void read__NullIRI_NoNamedGraphs_IRIsBelongToASingleDocument__ResolveIRIsUsingGenericRequestIRI() throws Exception {
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

		IRI expectedDocumentIRI = SimpleValueFactory.getInstance().createIRI( this.configurationRepository.forgeGenericRequestURL() );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 3 );
		assertEquals( document.getFragmentResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentIRI );
	}

	@Test
	public void read__NullIRI_SingleNamedGraph_IRIsBelongToASingleDocument__ResolveIRIsUsingGenericRequestIRI() throws Exception {
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

		IRI expectedDocumentIRI = SimpleValueFactory.getInstance().createIRI( this.configurationRepository.forgeGenericRequestURL() );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 3 );
		assertEquals( document.getFragmentResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentIRI );
	}

	@Test
	public void read__OnlyBlankNodes_NoNamedGraphs__CreateDocumentUsingGenericIRI() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"[" +
			"	a <http://example.org/ns#BlankNode>" +
			"].";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		IRI expectedDocumentIRI = SimpleValueFactory.getInstance().createIRI( this.configurationRepository.forgeGenericRequestURL() );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentIRI );
	}

	@Test
	public void read__OnlyBlankNodes_InNamedGraph__ReturnRDFDocumentWithNamedGraphIRI() throws Exception {
		RDFDocumentMessageConverter messageConverter = new RDFDocumentMessageConverter( this.configurationRepository );

		String body = "" +
			"<http://example.org/some-resource/> {" +
			"	[" +
			"		a <http://example.org/ns#BlankNode>" +
			"	]." +
			"}";

		HttpInputMessage inputMessage = prepareHTTPInputMessage( body, RDFFormat.TRIG );

		RDFDocument document = messageConverter.read( RDFDocument.class, inputMessage );

		IRI expectedDocumentIRI = SimpleValueFactory.getInstance().createIRI( "http://example.org/some-resource/" );
		assertNotNull( document );
		assertEquals( document.getResources().size(), 1 );

		List<Resource> contexts = document.stream().map( Statement::getContext ).distinct().collect( Collectors.toList() );
		assertEquals( contexts.size(), 1 );
		assertEquals( contexts.get( 0 ), expectedDocumentIRI );
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
