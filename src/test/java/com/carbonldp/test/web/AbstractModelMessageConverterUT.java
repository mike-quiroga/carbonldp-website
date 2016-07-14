package com.carbonldp.test.web;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.mail.MailSettings;
import com.carbonldp.test.AbstractUT;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.web.converters.AbstractModelMessageConverter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import static com.carbonldp.Consts.*;
import static org.testng.Assert.*;

public class AbstractModelMessageConverterUT extends AbstractUT {
	AbstractModel model;
	AbstractModel modelRead;
	MediaType mediaTypeAcceptable;
	MediaType mediaTypeNotAcceptable;
	MediaType mediaTypeAll;
	AbstractModelMessageConverter messageConverter;
	ConfigurationRepository configurationRepository;
	HttpInputMessage inputMessage;
	HttpOutputMessage outputMessage;
	InputStream inputStream;
	MockOutputStreamImpl outputStream;
	ValueFactory factory = SimpleValueFactory.getInstance();
	IRI subj;
	IRI pred;
	IRI obj;

	@Override
	protected void setUp() {
		mediaTypeAcceptable = new MediaType( "application", "xml" );
		mediaTypeNotAcceptable = new MediaType( "String" );
		mediaTypeAll = new MediaType( "*" );
		configurationRepository = new ConfigurationRepositoryImpl();
		messageConverter = new AbstractModelMessageConverter( configurationRepository );

		model = new LinkedHashModel();
		subj = factory.createIRI( "http://example.org/rob" );
		pred = factory.createIRI( "http://example.org/is-a" );
		obj = factory.createIRI( "http://example.org/stark" );
		model.add( subj, pred, obj );
		subj = factory.createIRI( "http://example.org/rob" );
		pred = factory.createIRI( "http://example.org/lives-in" );
		obj = factory.createIRI( "http://example.org/winterfell" );
		model.add( subj, pred, obj );
	}

	@Test
	public void canReadTest() {
		assertTrue( messageConverter.canRead( LinkedHashModel.class, mediaTypeAcceptable ) );
		assertFalse( messageConverter.canRead( LinkedHashModel.class, mediaTypeNotAcceptable ) );
		assertTrue( messageConverter.canRead( LinkedHashModel.class, mediaTypeAll ) );
	}

	@Test
	public void canWriteTest() {
		assertTrue( messageConverter.canRead( AbstractModel.class, mediaTypeAcceptable ) );
		assertFalse( messageConverter.canRead( AbstractModel.class, mediaTypeNotAcceptable ) );
		assertTrue( messageConverter.canRead( AbstractModel.class, mediaTypeAll ) );

		for ( MediaType supportedMediaType : messageConverter.getSupportedMediaTypes() ) {
			System.out.println( supportedMediaType.getType() + " " + supportedMediaType.getSubtype() );
		}
	}

	@Test
	public void readTest() throws Exception {
		inputMessage = new MockHttpInputMessage( ModelUtil.getInputStream( model ) );
		modelRead = messageConverter.read( LinkedHashModel.class, inputMessage );

		assertTrue( model.equals( modelRead ) );

		model.clear();

		inputMessage = new MockHttpInputMessage( ModelUtil.getInputStream( model ) );
		modelRead = messageConverter.read( LinkedHashModel.class, inputMessage );

		assertEquals( modelRead.size(), 0 );
	}

	@Test
	public void writeTest() {
		model.clear();
		subj = factory.createIRI( "http://example.org/rob" );
		pred = factory.createIRI( "http://example.org/is-a" );
		obj = factory.createIRI( "http://example.org/stark" );
		model.add( subj, pred, obj );
		subj = factory.createIRI( "http://example.org/rob" );
		pred = factory.createIRI( "http://example.org/lives-in" );
		obj = factory.createIRI( "http://example.org/winterfell" );
		model.add( subj, pred, obj );

		outputStream = new MockOutputStreamImpl();
		outputMessage = new MockHttpOutputMessage( outputStream );
		try {
			messageConverter.write( model, outputMessage.getHeaders().getContentType(), outputMessage );
			assertTrue( outputStream.getFlush() );
		} catch ( HttpMessageNotWritableException | IOException e ) {
			throw new RuntimeException( "model nor propertly initialized", e );
		}

	}

}

class MockOutputStreamImpl extends OutputStream {
	protected byte[] buf;
	protected int count;
	protected boolean flush;

	public boolean getFlush() {
		return flush;
	}

	public MockOutputStreamImpl() {
	}

	@Override
	public void write( int b ) throws IOException {
		// TODO Auto-generated method stub
		flush = false;
		if ( buf == null ) {
			buf = new byte[1];
			count = 1;
		} else {
			byte[] newBuf = new byte[buf.length + 1];
			for ( int i = 0; i < buf.length; i++ ) {
				newBuf[i] = buf[i];
			}
			b &= 255;
			newBuf[buf.length] = (byte) b;
			buf = newBuf;
		}

	}

	public void flush() {
		flush = true;
		System.out.println( "" + buf.length );
	}

}

class ConfigurationRepositoryImpl implements ConfigurationRepository {

	private String appsRepositoryDirectory = "http://local.carbonldp.com/";
	private String platformRepositoryDirectory = "http://local.carbonldp.com/";
	private String platformURL = "http://local.carbonldp.com/";
	private String platformContainer = "http://local.carbonldp.com/";
	private String platformContainerURL = "http://local.carbonldp.com/";
	private String platformAppsContainer = "http://local.carbonldp.com/";
	private String platformAppsContainerURL = "http://local.carbonldp.com/";
	private String platformAgentsContainer = "http://local.carbonldp.com/";
	private String platformAgentsContainerURL = "http://local.carbonldp.com/";
	private String platformRolesContainer = "http://local.carbonldp.com/";
	private String platformRolesContainerURL = "http://local.carbonldp.com/";
	private String platformPrivilegesContainer = "http://local.carbonldp.com/";
	private String platformPrivilegesContainerURL = "http://local.carbonldp.com/";
	private String applicationsEntryPoint = "http://local.carbonldp.com/";
	private String applicationsEntryPointURL = "http://local.carbonldp.com/";
	private String genericRequest = "http://local.carbonldp.com/";
	private String genericRequestURL = "http://local.carbonldp.com/";
	private String realmName = "http://local.carbonldp.com/";
	private Boolean _enforceEndingSlash = true;
	private Random random;

	public ConfigurationRepositoryImpl() {
		this.random = new Random();
	}

	public String getPlatformRepositoryDirectory() {
		return platformRepositoryDirectory;
	}

	public String getAppsRepositoryDirectory() {
		return appsRepositoryDirectory;
	}

	public String getPlatformURL() {
		return platformURL;
	}

	public String getPlatformContainer() {
		return platformContainer;
	}

	public String getPlatformContainerURL() {
		return platformContainerURL;
	}

	public String getPlatformAppsContainer() {
		return platformAppsContainer;
	}

	public String getPlatformAppsContainerURL() {
		return platformAppsContainerURL;
	}

	public String getPlatformAgentsContainer() {
		return platformAgentsContainer;
	}

	public String getPlatformAgentsContainerURL() {
		return platformAgentsContainerURL;
	}

	public String getApplicationsEntryPoint() {
		return applicationsEntryPoint;
	}

	public String getApplicationsEntryPointURL() {
		return applicationsEntryPointURL;
	}

	public String getRealmName() {
		return realmName;
	}

	@Override
	public boolean isGenericRequest( String uri ) {
		AntPathMatcher matcher = new AntPathMatcher();
		uri = uri.replace( getPlatformURL(), SLASH );

		return matcher.match( getGenericRequestPattern(), uri );
	}

	@Override
	public String getGenericRequestSlug( String uri ) {
		AntPathMatcher matcher = new AntPathMatcher();
		uri = uri.replace( getPlatformURL(), EMPTY_STRING );

		// The matcher removes the ending slash (if it finds one)
		boolean hasTrailingSlash = uri.endsWith( SLASH );

		uri = matcher.extractPathWithinPattern( getGenericRequestPattern(), uri );

		int index = uri.indexOf( SLASH );
		if ( index == - 1 ) {
			// The timestamp is the last piece of the generic request IRI
			return null;
		}
		if ( ( index + 1 ) == uri.length() ) {
			// "/" is the last character
			return null;
		}

		StringBuilder slugBuilder = new StringBuilder();
		slugBuilder.append( uri.substring( index + 1 ) );
		if ( hasTrailingSlash ) slugBuilder.append( SLASH );

		return slugBuilder.toString();
	}

	public String forgeGenericRequestURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append( this.genericRequestURL ).append( random.nextLong() );
		if ( enforceEndingSlash() ) urlBuilder.append( SLASH );
		return urlBuilder.toString();
	}

	private String getGenericRequestPattern() {
		StringBuilder patternBuilder = new StringBuilder();
		if ( ! this.genericRequest.startsWith( SLASH ) ) patternBuilder.append( SLASH );
		patternBuilder.append( this.genericRequest );
		if ( ! this.genericRequest.endsWith( SLASH ) ) patternBuilder.append( SLASH );
		patternBuilder.append( "?*/**/" );
		return patternBuilder.toString();
	}

	public Boolean enforceEndingSlash() {
		return _enforceEndingSlash;
	}

	@Override
	public boolean requireAgentEmailValidation() {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	@Override
	public MailSettings getMailSettings() {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

}

class MockHttpOutputMessage implements HttpOutputMessage {
	private final HttpHeaders headers = new HttpHeaders();
	private final OutputStream body;

	public MockHttpOutputMessage( OutputStream body ) {
		this.body = body;
		headers.setContentType( new MediaType( "application", "ld+json" ) );
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}

	@Override
	public OutputStream getBody() throws IOException {
		// TODO Auto-generated method stub
		return body;
	}

}

class MockHttpInputMessage implements HttpInputMessage {

	private final HttpHeaders headers = new HttpHeaders();

	private final InputStream body;

	public MockHttpInputMessage( byte[] contents ) {
		this.body = ( contents != null ) ? new ByteArrayInputStream( contents ) : null;
	}

	public MockHttpInputMessage( InputStream body ) {
		Assert.notNull( body, "'body' must not be null" );
		this.body = body;
		headers.setContentType( new MediaType( "application", "ld+json" ) );
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		return this.body;
	}
}
