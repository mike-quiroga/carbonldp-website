package com.base22.carbon.authentication.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

import com.base22.carbon.authentication.AgentTokenAuthenticationToken;
import com.base22.carbon.authentication.KeyAuthenticationToken;

public class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	protected AuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	static final Logger LOG = LoggerFactory.getLogger(AuthenticationProcessingFilter.class);

	private AuthenticationManager authenticationManager;

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(authenticationManager, "authenticationManager must be specified");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

		if ( LOG.isDebugEnabled() ) {
			LOG.debug(">> doFilter() > Triggered filter.");
		}
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		Authentication authentication = null;
		try {
			authentication = attemptAuthentication(request, response);
			if ( authentication == null ) {
				chain.doFilter(request, response);
				return;
			}
		} catch (AuthenticationServiceException exception) {
			unsuccessfulAuthentication(request, response, exception);
			return;
		} catch (AuthenticationException exception) {
			unsuccessfulAuthentication(request, response, exception);
			return;
		}

		if ( authentication != null ) {
			successfulAuthentication(request, response, chain, authentication);
		}
		chain.doFilter(request, response);
	}

	// TODO: Move all the constants to a better place
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException,
			ServletException {
		AbstractAuthenticationToken authentication = null;

		if ( request.getHeader("X-Carbon-Auth-Method") != null ) {
			authentication = handleCarbonHeaderAuthentication(request);
		} else {
			Cookie tokenCookie = getTokenCookie(request);
			if ( tokenCookie != null ) {
				authentication = new AgentTokenAuthenticationToken(tokenCookie.getValue());
			}
		}

		if ( authentication != null ) {
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			authentication = (AbstractAuthenticationToken) authenticationManager.authenticate(authentication);
		}

		return authentication;
	}

	private AbstractAuthenticationToken handleCarbonHeaderAuthentication(HttpServletRequest request) {
		AbstractAuthenticationToken authentication = null;

		String authenticationMethod = request.getHeader("X-Carbon-Auth-Method");

		if ( authenticationMethod != null ) {
			if ( authenticationMethod.equals("username") ) {
				String username = request.getHeader("X-Carbon-Agent-Username");
				String password = request.getHeader("X-Carbon-Agent-Password");

				if ( username != null && password != null ) {
					authentication = new UsernamePasswordAuthenticationToken(username, password);
				}
			} else if ( authenticationMethod.equals("key") ) {
				String key = request.getHeader("X-Carbon-Agent-Key");
				if ( key != null ) {
					authentication = new KeyAuthenticationToken(key);
				}
			} else if ( authenticationMethod.equals("token") ) {
				String token = request.getHeader("X-Carbon-Agent-Token");
				if ( token != null ) {
					authentication = new AgentTokenAuthenticationToken(token);
				}
			}
		}
		return authentication;
	}

	private Cookie getTokenCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if ( cookies == null ) {
			return null;
		}
		if ( cookies.length == 0 ) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if ( cookie.getName().equals("TOKEN") ) {
				return cookie;
			}
		}
		return null;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
}