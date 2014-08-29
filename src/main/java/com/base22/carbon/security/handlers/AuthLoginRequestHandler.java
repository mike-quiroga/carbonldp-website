package com.base22.carbon.security.handlers;

import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.util.CookieGenerator;

import com.base22.carbon.constants.APIPreferences.AuthenticationPreference;
import com.base22.carbon.constants.HttpHeaders;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.models.LDPResource;
import com.base22.carbon.models.LDPResourceFactory;
import com.base22.carbon.models.PrefixedURI;
import com.base22.carbon.models.RDFPropertyEnum;
import com.base22.carbon.models.RDFResourceEnum;
import com.base22.carbon.security.tokens.AgentAuthenticationToken;
import com.base22.carbon.services.ConfigurationService;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class AuthLoginRequestHandler {

	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	private TokenService tokenService;

	static final Logger LOG = LoggerFactory.getLogger(AuthLoginRequestHandler.class);

	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws CarbonException {
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

		Enumeration<String> preferHeaders = request.getHeaders(HttpHeaders.PREFER);
		HttpHeader preferHeader = new HttpHeader(preferHeaders);
		if ( preferCookie(preferHeader) ) {
			setTokenCookie(token, response);
		}

		LDPResource tokenResource = getTokenResource(token);

		return new ResponseEntity<Object>(tokenResource, HttpStatus.OK);
	}

	private void setTokenCookie(Token token, HttpServletResponse response) {
		CookieGenerator generator = new CookieGenerator();
		generator.setCookieDomain(configurationService.getServerDomain());
		generator.setCookieMaxAge(60 * configurationService.getTokenCookieLifeInMinutes());
		generator.setCookieName("TOKEN");

		generator.removeCookie(response);
		generator.addCookie(response, token.getKey());
	}

	private boolean preferCookie(HttpHeader preferHeader) {
		boolean cookie = false;
		List<HttpHeaderValue> returnPreferences = HttpHeader.filterHeaderValues(preferHeader, "return", null, null, null);

		for (HttpHeaderValue returnPreference : returnPreferences) {
			String returnValue = returnPreference.getMainValue();
			if ( returnValue != null ) {
				if ( AuthenticationPreference.findByURI(returnValue) == AuthenticationPreference.COOKIE ) {
					cookie = true;
				}
			}
		}
		return cookie;
	}

	private LDPResource getTokenResource(Token token) {

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(configurationService.getServerURL())
			.append("/responses/")
			.append(String.valueOf(DateTime.now().getMillis()))
		;
		//@formatter:on

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(uriBuilder.toString());

		LDPResource tokenResource = null;
		LDPResourceFactory factory = new LDPResourceFactory();
		try {
			tokenResource = factory.create(resource);
		} catch (CarbonException e) {
			// TODO: FT
		}

		tokenResource.addType(Resources.CLASS.getResource());
		tokenResource.setProperty(Properties.KEY.getProperty(), token.getKey());
		// TODO: Set expire date

		return tokenResource;
	}

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("cs", "Token")
		);
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;
		private final Resource[] resources;

		Resources(PrefixedURI... uris) {
			this.prefixedURIs = uris;

			this.resources = new Resource[uris.length];
			for (int i = 0; i < uris.length; i++) {
				this.resources[i] = ResourceFactory.createResource(uris[i].getURI());
			}
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURIs[0];
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Resource getResource() {
			return this.resources[0];
		}

		public Resource[] getResources() {
			return this.resources;
		}

		public static Resources findByURI(String uri) {
			for (Resources resource : Resources.values()) {
				for (PrefixedURI resourceURI : resource.getPrefixedURIs()) {
					if ( resourceURI.getURI().equals(uri) || resourceURI.getShortVersion().equals(uri) ) {
						return resource;
					}
				}
			}
			return null;
		}
	}

	// TODO: Finish Vocabulary
	public static enum Properties implements RDFPropertyEnum {
		//@formatter:off
		KEY(
			new PrefixedURI("cs", "key")
		),
		EXPIRE_DATE(
			new PrefixedURI("cs", "expireDate")
		);
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;
		private final Property[] properties;

		Properties(PrefixedURI... uris) {
			this.prefixedURIs = uris;

			this.properties = new Property[uris.length];
			for (int i = 0; i < uris.length; i++) {
				this.properties[i] = ResourceFactory.createProperty(uris[i].getURI());
			}
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURIs[0];
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Property getProperty() {
			return this.properties[0];
		}

		public static Properties findByURI(String uri) {
			for (Properties property : Properties.values()) {
				for (PrefixedURI propertyURI : property.getPrefixedURIs()) {
					if ( propertyURI.getURI().equals(uri) || propertyURI.getShortVersion().equals(uri) ) {
						return property;
					}
				}
			}
			return null;
		}
	}

}
