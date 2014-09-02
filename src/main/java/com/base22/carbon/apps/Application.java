package com.base22.carbon.apps;

import java.util.HashSet;
import java.util.UUID;

import com.base22.carbon.CarbonException;
import com.base22.carbon.PrefixedURI;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.ldp.LDPResource;
import com.base22.carbon.models.RDFPropertyEnum;
import com.base22.carbon.models.RDFRepresentable;
import com.base22.carbon.models.RDFResourceEnum;
import com.base22.carbon.models.UUIDObject;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Application extends UUIDObject implements RDFRepresentable<RDFApplication> {

	private UUID datasetUuid;
	private String slug;
	private String name;
	private String masterKey;

	private HashSet<ApplicationRole> applicationRoles;

	public UUID getDatasetUuid() {
		return datasetUuid;
	}

	public void setDatasetUuid(UUID datasetUuid) {
		this.datasetUuid = datasetUuid;
	}

	public void setDatasetUuid(String datasetName) {
		this.datasetUuid = AuthenticationUtil.restoreUUID(datasetName);
	}

	public String getDatasetName() {
		if ( datasetUuid != null ) {
			return this.datasetUuid.toString();
		} else {
			return null;
		}
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getIdentifier() {
		if ( this.slug != null ) {
			return this.slug;
		} else {
			return getUuidString();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMasterKey() {
		return masterKey;
	}

	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	public HashSet<ApplicationRole> getApplicationRoles() {
		return applicationRoles;
	}

	public void setApplicationRoles(HashSet<ApplicationRole> applicationRoles) {
		this.applicationRoles = applicationRoles;
	}

	public static final String ENDPOINT = "/apps/";

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("cs", "Application")
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
		),
		SLUG(
			new PrefixedURI("c", "slug")
		),
		NAME(
			new PrefixedURI("doap", "name")
		),
		MASTER_KEY(
			new PrefixedURI("cs", "masterKey")
		),
		DOMAIN(
			new PrefixedURI("c", "application")
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
	public void recoverFromLDPR(LDPResource ldpResource) throws CarbonException {
		RDFApplicationFactory factory = new RDFApplicationFactory();
		RDFApplication rdfApplication = factory.create(ldpResource.getResource());

		this.setUuid(rdfApplication.getUUID());
		this.setSlug(rdfApplication.getSlug());
		this.setName(rdfApplication.getName());

	}

	@Override
	public RDFApplication createRDFRepresentation() {
		RDFApplicationFactory factory = new RDFApplicationFactory();
		return factory.create(this);
	}
}
