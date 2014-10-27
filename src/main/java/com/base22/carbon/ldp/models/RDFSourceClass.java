package com.base22.carbon.ldp.models;

import com.base22.carbon.Carbon;
import com.base22.carbon.models.PrefixedURI;
import com.base22.carbon.models.RDFPropertyEnum;
import com.base22.carbon.models.RDFResourceEnum;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class RDFSourceClass {
	public static final String TYPE = Carbon.CONFIGURED_PREFIXES.get("ldp").concat("RDFSource");
	public static final String LINK_TYPE = "<" + TYPE + ">; rel=\"type\"";

	public static final String CREATED = Carbon.CONFIGURED_PREFIXES.get("c") + "created";
	public static final Property CREATED_P = ResourceFactory.createProperty(CREATED);

	public static final String MODIFIED = Carbon.CONFIGURED_PREFIXES.get("c") + "modified";
	public static final Property MODIFIED_P = ResourceFactory.createProperty(MODIFIED);

	public static final String HAS_ACCESS_POINT = Carbon.CONFIGURED_PREFIXES.get("c") + "accessPoint";
	public static final Property HAS_ACCESS_POINT_P = ResourceFactory.createProperty(HAS_ACCESS_POINT);

	// TODO: Move this?
	public static final String ACCESS_POINT_CLASS = Carbon.CONFIGURED_PREFIXES.get("c") + "AccessPoint";
	public static final String ACCESS_POINT_PREFIX = Carbon.SYSTEM_RESOURCE_SIGN + "accessPoint-";
	public static final String CONTAINER = Carbon.CONFIGURED_PREFIXES.get("c") + "container";
	public static final Property CONTAINER_P = ResourceFactory.createProperty(CONTAINER);

	public static final String FOR_PROPERTY = Carbon.CONFIGURED_PREFIXES.get("c") + "forProperty";
	public static final Property FOR_PROPERTY_P = ResourceFactory.createProperty(FOR_PROPERTY);

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("ldp", "RDFSource")
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
		CREATED(
			new PrefixedURI("c", "created")
		),
		MODIFIED(
			new PrefixedURI("c", "modified")
		),
		HAS_ACCESS_POINT(
			new PrefixedURI("c", "accessPoint")
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