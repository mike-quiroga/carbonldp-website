package com.carbonldp.test.apps.context;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextPersistanceFilter;
import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.ActionCallback;

public class AppContextIT extends AbstractIT {

	@Autowired
	private AppContextPersistanceFilter appContextPersistanceFilter;

	@Autowired
	private AppRepository appRepository;

	static final String FILTER_APPLIED = "__carbon_acpf_applied";

	AppContext context = AppContextHolder.createEmptyContext();

	@Test
	public void avoidFilterSecondTimeTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		FilterChain chain = Mockito.mock(FilterChain.class);

		Mockito.when(request.getAttribute(FILTER_APPLIED)).thenReturn(true);
		try {
			appContextPersistanceFilter.doFilter(request, response, chain);
		} catch (IOException | ServletException e) {
			throw new RuntimeException(e);
		}
		Mockito.verify(request, Mockito.never()).getRequestURI();
		context.setApplication(null);
	}

	@Test
	public void wrongRequestURITest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		FilterChain chain = Mockito.mock(FilterChain.class);

		Mockito.when(request.getAttribute(FILTER_APPLIED)).thenReturn(null);
		Mockito.when(request.getRequestURI()).thenReturn("something");
		try {
			appContextPersistanceFilter.doFilter(request, response, chain);
		} catch (IOException | ServletException e) {
			throw new RuntimeException(e);
		}
		Mockito.verify(response).setStatus(404);
		context.setApplication(null);
	}

	@Test
	public void appNotFoundTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		FilterChain chain = Mockito.mock(FilterChain.class);

		Mockito.when(request.getAttribute(FILTER_APPLIED)).thenReturn(null);
		Mockito.when(request.getRequestURI()).thenReturn("apps/some-blog/");
		try {
			appContextPersistanceFilter.doFilter(request, response, chain);
		} catch (IOException | ServletException e) {
			throw new RuntimeException(e);
		}
		Mockito.verify(response).setStatus(404);
		context.setApplication(null);
	}

	@Test
	public void plattformToAppContextExchangerTest() {
		App app = appRepository.findByRootContainer(new URIImpl("http://local.carbonldp.com/apps/test-blog/"));
		context.setApplication(null);
		assertTrue(context.isEmpty());
		applicationContextTemplate.runInAppContext(app, new ActionCallback() {
			@Override
			public void run() {
				assertEquals(AppContextHolder.getContext().getApplication().getURI().stringValue(), "http://local.carbonldp.com/apps/test-blog/");

			}

		});
		context.setApplication(null);
	}

	@Test
	public void appToPlatformContextExchangerTest() {
		App app = appRepository.findByRootContainer(new URIImpl("http://local.carbonldp.com/apps/test-blog/"));
		context.setApplication(app);
		app = AppContextHolder.getContext().getApplication();
		assertEquals(app.getURI().stringValue(), "http://local.carbonldp.com/apps/test-blog/");

		platformContextTemplate.runInPlatformContext(new ActionCallback() {
			@Override
			public void run() {
				assertTrue(AppContextHolder.getContext().isEmpty());
			}
		});
		context.setApplication(null);
	}

	@Test
	public void successfullAppContextEnableTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		FilterChain chain = new ChainMock();

		Mockito.when(request.getAttribute(FILTER_APPLIED)).thenReturn(null);
		Mockito.when(request.getRequestURI()).thenReturn("apps/test-blog/");
		try {
			appContextPersistanceFilter.doFilter(request, response, chain);
		} catch (IOException | ServletException e) {
			throw new RuntimeException(e);
		}
		Mockito.verify(request).removeAttribute(FILTER_APPLIED);
		context.setApplication(null);
	}

}

class ChainMock implements FilterChain {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		App app = AppContextHolder.getContext().getApplication();
		assertEquals(app.getURI().stringValue(), "http://local.carbonldp.com/apps/test-blog/");
	}
}
