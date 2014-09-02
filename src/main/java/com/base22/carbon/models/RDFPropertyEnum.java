package com.base22.carbon.models;

import com.base22.carbon.PrefixedURI;
import com.hp.hpl.jena.rdf.model.Property;

public interface RDFPropertyEnum {
	public PrefixedURI getPrefixedURI();

	public PrefixedURI[] getPrefixedURIs();

	public Property getProperty();

	//@formatter:off
	/*
		//@formatter:off
		UUID(
			new PrefixedURI("c", "uuid"),
			new PrefixedURI("c", "uuid")
		),
			
		;
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
	 */
	// @formatter:on
}
