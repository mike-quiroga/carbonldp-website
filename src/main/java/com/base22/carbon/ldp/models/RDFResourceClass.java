package com.base22.carbon.ldp.models;

import com.base22.carbon.Carbon;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class RDFResourceClass {
	public static enum Resources {
		//@formatter:off
		CLASS(Carbon.CONFIGURED_PREFIXES.get("ldp"), "Resource");
		//@formatter:on

		private String prefix;
		private String slug;
		private String uri;
		private Resource resource;
		private String type;

		Resources(String prefix, String slug) {
			this.prefix = prefix;
			this.slug = slug;
			this.uri = prefix.concat(slug);
			this.resource = ResourceFactory.createResource(this.uri);
			this.type = "<" + this.uri + ">; rel=\"type\"";
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

		public String getType() {
			return this.type;
		}
	}

	public static enum Properties {
		//@formatter:off
		RDF_TYPE(Carbon.CONFIGURED_PREFIXES.get("rdf"), "type"),
		HAS_ACL(Carbon.CONFIGURED_PREFIXES.get("acl"), "accessControl");
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
}
