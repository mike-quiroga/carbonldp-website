package com.base22.carbon.constants;

import com.base22.carbon.models.PrefixedURI;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class LDPNR {
	public static final String TYPE = Carbon.CONFIGURED_PREFIXES.get("c") + "WrapperForLDPNR";
	public static final String LINK_TYPE = "<" + TYPE + ">; rel=\"type\"";

	public static final String NR_TYPE = Carbon.CONFIGURED_PREFIXES.get("ldp") + "NonRDFSource";
	public static final String NR_LINK_TYPE = "<" + NR_TYPE + ">; rel=\"type\"";

	public static final String FILE_NAME = Carbon.CONFIGURED_PREFIXES.get("c") + "file";
	public static final Property FILE_NAME_P = ResourceFactory.createProperty(FILE_NAME);

	public static final String FILE_ORIGINAL_NAME = Carbon.CONFIGURED_PREFIXES.get("c") + "name";
	public static final Property FILE_ORIGINAL_NAME_P = ResourceFactory.createProperty(FILE_ORIGINAL_NAME);

	public static final String FILE_EXTENSION = Carbon.CONFIGURED_PREFIXES.get("c") + "extension";
	public static final Property FILE_EXTENSION_P = ResourceFactory.createProperty(FILE_EXTENSION);

	public static final String FILE_FORMAT = Carbon.CONFIGURED_PREFIXES.get("dc") + "format";
	public static final Property FILE_FORMAT_P = ResourceFactory.createProperty(FILE_FORMAT);

	public static final String FILE_SIZE = Carbon.CONFIGURED_PREFIXES.get("c") + "file";
	public static final Property FILE_SIZE_P = ResourceFactory.createProperty(FILE_SIZE);

	public static enum Resources {
		//@formatter:off
		WRAPPER(
			new PrefixedURI("c", "WrapperForLDPNR")
		),
		LDPNR(
			new PrefixedURI("ldp", "NonRDFSource")
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

		public String getURI() {
			return prefixedURI.getURI();
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
		FILE_NAME(Carbon.CONFIGURED_PREFIXES.get("c"), "file"),
		FILE_ORIGINAL_NAME(Carbon.CONFIGURED_PREFIXES.get("c"), "name"),
		FILE_EXTENSION(Carbon.CONFIGURED_PREFIXES.get("c"), "extension"),
		FILE_FORMAT(Carbon.CONFIGURED_PREFIXES.get("dc"), "format"),
		FILE_SIZE(Carbon.CONFIGURED_PREFIXES.get("c"), "size");
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
