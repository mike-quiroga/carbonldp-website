package com.carbonldp.test.web.cors;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.namespaces.C;
import com.carbonldp.namespaces.CS;
import com.carbonldp.namespaces.XSD;
import com.carbonldp.test.AbstractIT;
import org.mockito.Mockito;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORSAppContextFilterIT extends AbstractIT {
	@Autowired
	@Qualifier( "corsAppContextFilter" )
	private Filter corsAppContextFilter;

	ValueFactory valueFactory = new ValueFactoryImpl();

	@Autowired
	private AppRepository appRepository;

	AppContext context;
	App app;

	public CORSAppContextFilterIT() {
		context = AppContextHolder.createEmptyContext();
	}

	private void setUp() {
		if ( ! appService.exists( new URIImpl( testResourceURI ) ) )
			throw new RuntimeException( "App not found" );
		app = appRepository.findByRootContainer( new URIImpl( testResourceURI ) );
		app.addDomain( valueFactory.createLiteral( "http://www.test.com/", new URIImpl( XSD.Properties.STRING ) ) );
		app.addDomain( valueFactory.createLiteral( "(http://|https://)www\\.regex\\d\\.com/[\\s\\S]*", new URIImpl( C.Classes.REGULAR_EXPRESSION ) ) );
		context.setApplication( null );
	}

	@Test
	public void filterAllowExactDomainTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );
		Mockito.when( request.getHeader( "Origin" ) ).thenReturn( "http://www.test.com/" );
		Mockito.when( request.getHeader( "Access-Control-Request-Method" ) ).thenReturn( "OPTIONS" );

		setUp();

		applicationContextTemplate.runInAppContext( app, () -> {
			try {
				corsAppContextFilter.doFilter( request, response, chain );
			} catch ( Exception e ) {
				throw new RuntimeException( "Filter not executed correctly", e );
			}
		} );
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Origin", "http://www.test.com/" );
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS" );
		context.setApplication( null );
	}

	@Test
	public void filterDenyTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );
		Mockito.when( request.getHeader( "Origin" ) ).thenReturn( "http://www.not-valid.com/" );
		Mockito.when( request.getHeader( "Access-Control-Request-Method" ) ).thenReturn( "OPTIONS" );

		setUp();

		applicationContextTemplate.runInAppContext( app, () -> {
			try {
				corsAppContextFilter.doFilter( request, response, chain );
			} catch ( Exception e ) {
				throw new RuntimeException( "Filter not executed correctly", e );
			}
		} );
		Mockito.verify( response, Mockito.never() ).addHeader( "Access-Control-Allow-Origin", "http://www.test.com/" );
		Mockito.verify( response, Mockito.never() ).addHeader( "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS" );
		context.setApplication( null );

	}

	//TODO: accept all, check with complete address, check with regex

	@Test
	public void filterAllowRegExTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );
		Mockito.when( request.getHeader( "Origin" ) ).thenReturn( "http://www.regex8.com/blog/posts/post/comment/" );
		Mockito.when( request.getHeader( "Access-Control-Request-Method" ) ).thenReturn( "OPTIONS" );

		setUp();
		applicationContextTemplate.runInAppContext( app, () -> {
			try {
				corsAppContextFilter.doFilter( request, response, chain );
			} catch ( Exception e ) {
				throw new RuntimeException( "Filter not executed correctly", e );
			}
		} );
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Origin", "http://www.regex8.com/blog/posts/post/comment/" );
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS" );
		context.setApplication( null );
	}

	@Test
	public void filterAllowAllTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );
		Mockito.when( request.getHeader( "Origin" ) ).thenReturn( "http://www.all-allowed-origins.com/" );
		Mockito.when( request.getHeader( "Access-Control-Request-Method" ) ).thenReturn( "OPTIONS" );

		setUp();
		app.addDomain( new URIImpl( CS.Classes.ALL_ORIGINS ) );
		applicationContextTemplate.runInAppContext( app, () -> {
			try {
				corsAppContextFilter.doFilter( request, response, chain );
			} catch ( Exception e ) {
				throw new RuntimeException( "Filter not executed correctly", e );
			}
		} );
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Origin", "http://www.all-allowed-origins.com/" );
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS" );
		context.setApplication( null );
	}
}
