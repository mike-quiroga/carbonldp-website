package com.base22.carbon.ldp.models;

import com.base22.carbon.Carbon;
import com.base22.carbon.models.PrefixedURI;
import com.base22.carbon.models.RDFPropertyEnum;
import com.base22.carbon.models.RDFResourceEnum;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class AccessPointClass {

	public static final String SLUG_PREFIX = Carbon.SYSTEM_RESOURCE_SIGN + "accessPoint-";

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("c", "AccessPoint")
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
		CONTAINER(
			new PrefixedURI("c", "container")
		),
		FOR_PROPERTY(
			new PrefixedURI("c", "forProperty")
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
