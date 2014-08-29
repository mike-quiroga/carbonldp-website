package com.base22.carbon.security.models;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.LDPResource;
import com.base22.carbon.models.PrefixedURI;
import com.base22.carbon.models.RDFPropertyEnum;
import com.base22.carbon.models.RDFRepresentable;
import com.base22.carbon.models.RDFResourceEnum;
import com.base22.carbon.security.constants.AceSR;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

// This class needs to implement GrantedAuthority so it gets included in the collection of ACE subjects

public class ApplicationRole extends UUIDObject implements GrantedAuthority, RDFRepresentable<RDFApplicationRole> {
	private static final long serialVersionUID = 7497947873798339446L;

	private String slug;
	private String name;
	private String description;

	private UUID applicationUUID;
	private UUID parentUUID;
	private HashSet<ApplicationRole> childRoles;
	private HashSet<Agent> agents;
	private HashSet<Group> groups;

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getApplicationUUID() {
		return applicationUUID;
	}

	public void setApplicationUUID(UUID applicationUUID) {
		this.applicationUUID = applicationUUID;
	}

	public UUID getParentUUID() {
		return parentUUID;
	}

	public void setParentUUID(UUID parentUUID) {
		this.parentUUID = parentUUID;
	}

	public HashSet<ApplicationRole> getChildRoles() {
		return childRoles;
	}

	public void setChildRoles(HashSet<ApplicationRole> childRoles) {
		this.childRoles = childRoles;
	}

	public HashSet<Agent> getAgents() {
		return agents;
	}

	public void setAgents(HashSet<Agent> agents) {
		this.agents = agents;
	}

	public HashSet<Group> getGroups() {
		return groups;
	}

	public void setGroups(HashSet<Group> groups) {
		this.groups = groups;
	}

	@Override
	public String getAuthority() {
		return AceSR.SubjectType.APP_ROLE.getName() + ": " + this.getUuidString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationRole [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

	public static final String ENDPOINT = "/roles/";

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("cs", "ApplicationRole")
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
		DESCRIPTION(
			new PrefixedURI("dcterms", "description")
		),
		PARENT(
			new PrefixedURI("api", "parent")
		),
		APPLICATION(
			new PrefixedURI("cs", "application")
		),
		CHILD_ROLES(
			new PrefixedURI("c", "child")
		),
		AGENTS(
			new PrefixedURI("c", "hasAgent")
		),
		GROUPS(
			new PrefixedURI("c", "hasGroup")
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
		RDFApplicationRoleFactory factory = new RDFApplicationRoleFactory();
		RDFApplicationRole rdfRole = factory.create(ldpResource.getResource());

		this.setUuid(rdfRole.getUUID());
		this.setParentUUID(rdfRole.getParentUUID());
		this.setApplicationUUID(rdfRole.getApplicationUUID());
		this.setName(rdfRole.getName());
		this.setDescription(rdfRole.getDescription());
	}

	@Override
	public RDFApplicationRole createRDFRepresentation() {
		RDFApplicationRoleFactory factory = new RDFApplicationRoleFactory();
		return factory.create(this);
	}

}
