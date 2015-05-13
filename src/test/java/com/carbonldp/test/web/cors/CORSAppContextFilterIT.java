package com.carbonldp.test.web.cors;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.test.AbstractIT;
import org.mockito.Mockito;
import org.openrdf.model.impl.URIImpl;
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

	@Autowired
	private AppRepository appRepository;

	AppContext context = AppContextHolder.createEmptyContext();
	App app;

	private void setUp() {
		app = appRepository.findByRootContainer( new URIImpl( "http://local.carbonldp.com/apps/test-blog/" ) );
		app.addDomain( "http://www.test.com/" );
		context.setApplication( null );
	}

	@Test
	public void filterAllowTest() {
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
		Mockito.verify( response ).addHeader( "Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS" );
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
		Mockito.verify( response, Mockito.never() ).addHeader( "Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS" );
		context.setApplication( null );

	}
}
