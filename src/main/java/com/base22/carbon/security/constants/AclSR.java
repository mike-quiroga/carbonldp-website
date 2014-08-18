package com.base22.carbon.security.constants;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.models.PrefixedURI;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class AclSR {
	public static final String PREFIX = "acl";

	public static enum Resources {
		//@formatter:off
		CLASS(
			new PrefixedURI("cs", "AccessControlList")
		);
		//@formatter:on

		private final PrefixedURI prefixedURI;
		private final PrefixedURI[] prefixedURIs;
		private final Resource resource;

		Resources(PrefixedURI... uris) {
			this.prefixedURI = uris[0];
			this.prefixedURIs = uris;

			this.resource = ResourceFactory.createResource(this.prefixedURI.getURI());
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURI;
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Resource getResource() {
			return resource;
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

	public static enum Properties {
		//@formatter:off
		ACCESS_TO(Carbon.CONFIGURED_PREFIXES.get("acl"), "accessTo"),
		HAS_ACE(Carbon.CONFIGURED_PREFIXES.get("cs"), "accessControlEntry");
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
