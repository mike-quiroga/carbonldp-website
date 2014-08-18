package com.base22.carbon.security.controllers;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.CookieGenerator;

import com.base22.carbon.security.tokens.AgentAuthenticationToken;
import com.base22.carbon.services.ConfigurationService;

@Controller
public class LoginController {

	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	private TokenService tokenService;

	static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@PreAuthorize("isAnonymous()")
	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public String userLoginView(HttpServletRequest request, HttpServletResponse response) {
		return "user.login";
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "auth/login", method = RequestMethod.POST)
	public ResponseEntity<Object> authLogin(HttpServletRequest request, HttpServletResponse response) {
		Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
		if ( authenticationToken == null ) {
			// TODO: FT
		}

		if ( ! (authenticationToken instanceof AgentAuthenticationToken) ) {
			// TODO: FT
		}

		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		UUID agentUUID = agentToken.getPrincipal().getUuid();

		Token token = tokenService.allocateToken(agentUUID.toString());

		// TODO: Handle a parameter to specify if this is wanted
		setTokenCookie(token, response);

		return new ResponseEntity<Object>(token.getKey(), HttpStatus.OK);
	}

	private void setTokenCookie(Token token, HttpServletResponse response) {
		CookieGenerator generator = new CookieGenerator();
		generator.setCookieDomain(configurationService.getServerDomain());
		generator.setCookieMaxAge(60 * configurationService.getTokenCookieLifeInMinutes());
		generator.setCookieName("TOKEN");

		generator.removeCookie(response);
		generator.addCookie(response, token.getKey());
	}
}
