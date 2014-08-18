package com.base22.carbon.security.constants;

import com.base22.carbon.constants.Carbon;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class AceSR {
	public static final String PREFIX = "ace-";

	public static enum Resources {
		//@formatter:off
		CLASS(Carbon.CONFIGURED_PREFIXES.get("cs"), "AccessControlEntry");
		//@formatter:on

		private String prefix;
		private String slug;
		private String uri;
		private Resource resource;

		Resources(String prefix, String slug) {
			this.prefix = prefix;
			this.slug = slug;
			this.uri = prefix.concat(slug);
			this.resource = ResourceFactory.createResource(this.uri);
		}

		public String getPrefix() {
			return prefix;
		}

		public String getSlug() {
			return slug;
		}

		public String getUri() {
			return uri;
		}

		public Resource getResource() {
			return resource;
		}
	}

	public static enum Properties {
		//@formatter:off
		SUBJECT(Carbon.CONFIGURED_PREFIXES.get("cs"), "subject"),
		SUBJECT_TYPE(Carbon.CONFIGURED_PREFIXES.get("cs"), "subjectType"),
		MODE(Carbon.CONFIGURED_PREFIXES.get("acl"), "mode"),
		GRANTING(Carbon.CONFIGURED_PREFIXES.get("cs"), "granting");
		//@formatter:on

		private String prefix;
		private String slug;
		private String uri;
		private Property property;

		Properties(String prefix, String slug) {
			this.prefix = prefix;
			this.slug = slug;
			this.uri = prefix.concat(slug);
			this.property = ResourceFactory.createProperty(this.uri);
		}

		public String getPrefix() {
			return prefix;
		}

		public String getSlug() {
			return slug;
		}

		public String getUri() {
			return uri;
		}

		public Property getProperty() {
			return property;
		}
	}

	public static enum SubjectType {
		//@formatter:off
		AGENT("Agent", Carbon.CONFIGURED_PREFIXES.get("cs") + "Agent"),
		GROUP("Group", Carbon.CONFIGURED_PREFIXES.get("cs") + "Group"), 
		APP_ROLE("Application Role", Carbon.CONFIGURED_PREFIXES.get("cs") + "ApplicationRole"),
		PRIVILEGE("Privilege", Carbon.CONFIGURED_PREFIXES.get("cs") + "Privilege");
		//@formatter:on

		private String name;
		private String uri;
		private Resource resource;

		SubjectType(String name, String uri) {
			this.name = name;
			this.uri = uri;
			this.resource = ResourceFactory.createResource(uri);
		}

		public String getName() {
			return this.name;
		}

		public String getURI() {
			return this.uri;
		}

		public Resource getResource() {
			return this.resource;
		}

		public static SubjectType findByName(String name) {
			for (SubjectType type : SubjectType.values()) {
				if ( type.getName().equals(name) ) {
					return type;
				}
			}
			return null;
		}

		public static SubjectType findByURI(String uri) {
			for (SubjectType type : SubjectType.values()) {
				if ( type.getURI().equals(uri) ) {
					return type;
				}
			}
			return null;
		}
	}
}
