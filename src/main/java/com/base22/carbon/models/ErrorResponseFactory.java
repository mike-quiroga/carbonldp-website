package com.base22.carbon.models;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;

import com.base22.carbon.Carbon;
import com.base22.carbon.ldp.models.RDFResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ErrorResponseFactory extends RDFResourceFactory {

	public static final String BASE = "/errors/";

	public ErrorResponse create() {
		Model model = ModelFactory.createDefaultModel();

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(Carbon.URL)
			.append(BASE)
			.append((new DateTime()).getMillis())
		;
		//@formatter:on

		Resource resource = model.createResource(uriBuilder.toString());

		ErrorResponse errorResponse = new ErrorResponseImpl(resource);
		errorResponse.setType(Resources.CLASS.getResource());

		return errorResponse;
	}

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("c", "ErrorResponse")
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

	public static enum Properties implements RDFPropertyEnum {
		//@formatter:off
		HTTP_STATUS_CODE(
			new PrefixedURI("c", "httpStatusCode")
		),
		CARBON_CODE(
			new PrefixedURI("c", "carbonCode")
		),
		FRIENDLY_MESSAGE(
			new PrefixedURI("c", "friendlyMessage")
		),
		DEBUG_MESSAGE(
			new PrefixedURI("c", "debugMessage")
		),
		HAS_PARAMETER_ISSUE(
			new PrefixedURI("c", "hasParameterIssue")
		),
		HAS_HEADER_ISSUE(
			new PrefixedURI("c", "hasHeaderIssue")
		),
		HAS_ENTITY_BODY(
			new PrefixedURI("c", "hasEntityBodyIssue")
		),
		HAS_STACK_TRACE_ELEMENT(
			new PrefixedURI("c", "hasStackTraceElement")
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

	protected class ErrorResponseImpl extends LDPResourceImpl implements ErrorResponse {
		public ErrorResponseImpl(Resource resource) {
			super(resource);
		}

		@Override
		public List<String> getLinkTypes() {
			List<String> types = super.getLinkTypes();

			// TODO: Move this to the actual printer
			HttpHeaderValue header = new HttpHeaderValue();
			header.setMainValue(Resources.CLASS.getPrefixedURI().getResourceURI());
			header.setExtendingKey("rel");
			header.setExtendingValue("type");

			types.add(header.toString());

			return types;
		}

		public HttpStatus getHttpStatus() {
			Integer integer = this.getInteger(Properties.HTTP_STATUS_CODE.getProperty());
			if ( integer == null ) {
				return null;
			}

			try {
				return HttpStatus.valueOf(integer);
			} catch (Exception ignore) {
				return null;
			}
		}

		public void setHttpStatus(HttpStatus httpStatus) {
			this.setProperty(Properties.HTTP_STATUS_CODE.getProperty(), httpStatus.value());
		}

		public String getCarbonCode() {
			return this.getString(Properties.CARBON_CODE.getProperty());
		}

		public void setCarbonCode(String carbonCode) {
			this.setProperty(Properties.CARBON_CODE.getProperty(), carbonCode);
		}

		public String getFriendlyMessage() {
			return this.getString(Properties.FRIENDLY_MESSAGE.getProperty());
		}

		public void setFriendlyMessage(String friendlyMessage) {
			this.setProperty(Properties.FRIENDLY_MESSAGE.getProperty(), friendlyMessage);
		}

		public String getDebugMessage() {
			return this.getString(Properties.DEBUG_MESSAGE.getProperty());
		}

		public void setDebugMessage(String debugMessage) {
			this.setProperty(Properties.DEBUG_MESSAGE.getProperty(), debugMessage);
		}

		public void addElementToDebugStackTrace(String step) {
			this.addProperty(Properties.HAS_STACK_TRACE_ELEMENT.getProperty(), step);
		}

		public void addHeaderIssue(String key, String code, String description, String value) {
			// TODO: Implement
		}

		public void addParameterIssue(String key, String code, String description, String value) {
			// TODO: Implement
		}

		public void setEntityBodyIssue(String code, String description) {
			// TODO: Implement
		}

		public class ISSUES {
			final String TYPE_PARAMENTER = Carbon.CONFIGURED_PREFIXES.get("c").concat("ParameterIssue");
			final String TYPE_HEADER = Carbon.CONFIGURED_PREFIXES.get("c").concat("HeaderIssue");
			final String TYPE_ENTITY = Carbon.CONFIGURED_PREFIXES.get("c").concat("EntityBodyIssue");

			final String KEY = Carbon.CONFIGURED_PREFIXES.get("c").concat("key");
			final Property KEY_P = ResourceFactory.createProperty(KEY);

			final String CODE = Carbon.CONFIGURED_PREFIXES.get("c").concat("code");
			final Property CODE_P = ResourceFactory.createProperty(CODE);

			final String DESCRIPTION = Carbon.CONFIGURED_PREFIXES.get("c").concat("description");
			final Property DESCRIPTION_P = ResourceFactory.createProperty(DESCRIPTION);

			final String VALUE = Carbon.CONFIGURED_PREFIXES.get("c").concat("value");
			final Property VALUE_P = ResourceFactory.createProperty(VALUE);

		}

		public class Issue {
			private String key;
			private String code;
			private String description;
			private String value;

			public Issue(String key, String code, String description, String value) {
				this.key = key;
				this.code = code;
				this.description = description;
				this.value = value;
			}

			public Resource addIssueResourceToModel(Model model, String issueURI) {
				Resource resource = model.createResource(issueURI);

				if ( key != null ) {
					resource.addProperty(new ISSUES().KEY_P, key);
				}
				if ( code != null ) {
					resource.addProperty(new ISSUES().CODE_P, code);
				}
				if ( description != null ) {
					resource.addProperty(new ISSUES().DESCRIPTION_P, description);
				}
				if ( value != null ) {
					resource.addProperty(new ISSUES().VALUE_P, value);
				}

				return resource;
			}
		}

	}
}
