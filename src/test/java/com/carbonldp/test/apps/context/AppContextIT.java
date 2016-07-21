package com.carbonldp.test.apps.context;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextPersistenceFilter;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.ActionCallback;
import org.mockito.Mockito;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.testng.Assert.*;

public class AppContextIT extends AbstractIT {

	@Autowired
	private AppContextPersistenceFilter appContextPersistenceFilter;

	@Autowired
	private AppRepository appRepository;

	static final String FILTER_APPLIED = "__carbon_acpf_applied";

	AppContext context = AppContextHolder.createEmptyContext();

	@Test
	public void avoidFilterSecondTimeTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );

		Mockito.when( request.getAttribute( FILTER_APPLIED ) ).thenReturn( true );
		try {
			appContextPersistenceFilter.doFilter( request, response, chain );
		} catch ( IOException | ServletException e ) {
			throw new RuntimeException( e );
		}
		Mockito.verify( request, Mockito.never() ).getRequestURI();
		context.setApplication( null );
	}

	@Test
	public void wrongRequestIRITest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );

		Mockito.when( request.getAttribute( FILTER_APPLIED ) ).thenReturn( null );
		Mockito.when( request.getRequestURI() ).thenReturn( "something" );
		try {
			appContextPersistenceFilter.doFilter( request, response, chain );
		} catch ( IOException | ServletException e ) {
			throw new RuntimeException( e );
		}
		Mockito.verify( response ).setStatus( 404 );
		context.setApplication( null );
	}

	@Test
	public void appNotFoundTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = Mockito.mock( FilterChain.class );

		Mockito.when( request.getAttribute( FILTER_APPLIED ) ).thenReturn( null );
		Mockito.when( request.getRequestURI() ).thenReturn( "apps/some-blog/" );
		try {
			appContextPersistenceFilter.doFilter( request, response, chain );
		} catch ( IOException | ServletException e ) {
			throw new RuntimeException( e );
		}
		Mockito.verify( response ).setStatus( 404 );
		context.setApplication( null );
	}

	@Test
	public void plattformToAppContextExchangerTest() {
		App app = appRepository.findByRootContainer( SimpleValueFactory.getInstance().createIRI( testResourceIRI ) );
		context.setApplication( null );
		assertTrue( context.isEmpty() );
		applicationContextTemplate.runInAppContext( app, new ActionCallback() {
			@Override
			public void run() {
				assertEquals( AppContextHolder.getContext().getApplication().getIRI().stringValue(), testResourceIRI );

			}

		} );
		context.setApplication( null );
	}

	@Test
	public void appToPlatformContextExchangerTest() {
		App app = appRepository.findByRootContainer( SimpleValueFactory.getInstance().createIRI( testResourceIRI ) );
		context.setApplication( app );
		AppContextHolder.setContext( context );
		app = AppContextHolder.getContext().getApplication();
		assertEquals( app.getIRI().stringValue(), testResourceIRI );

		platformContextTemplate.runInPlatformContext( new ActionCallback() {
			@Override
			public void run() {
				assertTrue( AppContextHolder.getContext().isEmpty() );
			}
		} );
		context.setApplication( null );
	}

	@Test
	public void successfulAppContextEnableTest() {
		HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
		HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
		FilterChain chain = new ChainMock( testResourceIRI );

		Mockito.when( request.getAttribute( FILTER_APPLIED ) ).thenReturn( null );
		Mockito.when( request.getRequestURI() ).thenReturn( "apps/test-blog/" );
		try {
			appContextPersistenceFilter.doFilter( request, response, chain );
		} catch ( IOException | ServletException e ) {
			throw new RuntimeException( e );
		}
		Mockito.verify( request ).removeAttribute( FILTER_APPLIED );
		context.setApplication( null );
	}

}

class ChainMock implements FilterChain {
	String testResourceIRI;

	public ChainMock( String testResourceIRI ) {
		this.testResourceIRI = testResourceIRI;
	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response ) throws IOException, ServletException {
		// TODO Auto-generated method stub
		App app = AppContextHolder.getContext().getApplication();
		assertEquals( app.getIRI().stringValue(), testResourceIRI );
	}
}
