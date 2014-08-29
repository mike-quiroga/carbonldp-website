package com.base22.carbon.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.exceptions.FactoryException;
import com.base22.carbon.security.utils.AuthenticationUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RDFGenericRequestFactory extends LDPResourceFactory implements RDFResourceFactory<RDFGenericRequest> {

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("c", "GenericRequest")
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
		UUID(
			new PrefixedURI("c", "uuid")
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

	@Override
	public RDFGenericRequest create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isRDFGenericRequest(ldpResource) ) {
			throw new FactoryException("The resource isn't a GenericRequest object.");
		}
		return new RDFGenericRequestImpl(resource);
	}

	@Override
	public RDFGenericRequest create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
		if ( ! isRDFGenericRequest(ldpResource) ) {
			throw new FactoryException("The resource isn't a GenericRequest object.");
		}
		return new RDFGenericRequestImpl(ldpResource.getResource());
	}

	public boolean isRDFGenericRequest(LDPResource ldpResource) throws CarbonException {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	@Override
	public List<String> validate(RDFGenericRequest toValidate) {
		// TODO: IT
		return null;
	}

	protected class RDFGenericRequestImpl extends LDPResourceImpl implements RDFGenericRequest {

		public RDFGenericRequestImpl(Resource resource) {
			super(resource);
		}

		@Override
		public UUID[] getUUIDs() {
			String[] uuidStrings = this.getStringProperties(Properties.UUID.getProperty());
			List<UUID> uuids = new ArrayList<UUID>();

			for (String uuidString : uuidStrings) {
				if ( AuthenticationUtil.isUUIDString(uuidString) ) {
					uuids.add(AuthenticationUtil.restoreUUID(uuidString));
				}
			}

			return (UUID[]) uuids.toArray();
		}

	}
}
