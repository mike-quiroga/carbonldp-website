package com.base22.carbon.security.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.exceptions.FactoryException;
import com.base22.carbon.models.LDPResource;
import com.base22.carbon.models.LDPResourceFactory;
import com.base22.carbon.security.models.ApplicationRole.Properties;
import com.base22.carbon.security.models.ApplicationRole.Resources;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFApplicationRoleFactory extends LDPResourceFactory {

	public RDFApplicationRole create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isRDFApplicationRole(ldpResource) ) {
			throw new FactoryException("The resource isn't an ApplicationRole object.");
		}
		return new RDFApplicationRoleImpl(resource);
	}

	public RDFApplicationRole create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
		if ( ! isRDFApplicationRole(ldpResource) ) {
			throw new FactoryException("The resource isn't an ApplicationRole object.");
		}
		return new RDFApplicationRoleImpl(ldpResource.getResource());
	}

	public RDFApplicationRole create(ApplicationRole applicationRole) {
		Model model = ModelFactory.createDefaultModel();

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(Application.BASE_URI)
			.append(applicationRole.getApplicationUUID().toString())
			.append(ApplicationRole.BASE_URI)
			.append(applicationRole.getUuidString())
		;
		//@formatter:on

		Resource resource = model.createResource(uriBuilder.toString());

		RDFApplicationRoleImpl role = new RDFApplicationRoleImpl(resource);
		role.setType(Resources.CLASS.getResource());
		role.setUUID(applicationRole.getUuid());
		role.setApplicationUUID(applicationRole.getApplicationUUID());
		role.setParentUUID(applicationRole.getParentUUID());
		role.setName(applicationRole.getName());
		role.setDescription(applicationRole.getDescription());

		// TODO: Set the other properties

		return role;
	}

	public List<String> validate(RDFApplicationRole applicationRole) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isRDFApplicationRole(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	private class RDFApplicationRoleImpl extends LDPResourceImpl implements RDFApplicationRole {
		public RDFApplicationRoleImpl(Resource resource) {
			super(resource);
		}

		@Override
		public UUID getUUID() {
			return this.getUUIDProperty(Properties.UUID.getProperty());
		}

		@Override
		public void setUUID(UUID roleUUID) {
			this.setProperty(Properties.UUID.getProperty(), roleUUID);
		}

		@Override
		public String getName() {
			return this.getStringProperty(Properties.NAME.getProperty());
		}

		@Override
		public void setName(String name) {
			this.setProperty(Properties.NAME.getProperty(), name);
		}

		@Override
		public String getDescription() {
			return this.getStringProperty(Properties.DESCRIPTION.getProperty());
		}

		@Override
		public void setDescription(String description) {
			this.setProperty(Properties.DESCRIPTION.getProperty(), description);
		}

		@Override
		public UUID getApplicationUUID() {
			return this.getUUIDProperty(Properties.APPLICATION.getProperty());
		}

		@Override
		public void setApplicationUUID(UUID applicationUUID) {
			this.setProperty(Properties.APPLICATION.getProperty(), applicationUUID);
		}

		@Override
		public UUID getParentUUID() {
			return this.getUUIDProperty(Properties.PARENT.getProperty());
		}

		@Override
		public void setParentUUID(UUID parentUUID) {
			this.setProperty(Properties.PARENT.getProperty(), parentUUID);
		}

		@Override
		public List<UUID> getChildRolesUUID() {
			// TODO: Implement
			return null;
		}

		@Override
		public void setChildRolesUUID(List<UUID> childRolesUUID) {
			// TODO: Implement
		}

		@Override
		public List<UUID> getAgentsUUID() {
			// TODO: Implement
			return null;
		}

		@Override
		public void setAgentsUUID(List<UUID> agentsUUID) {
			// TODO: Implement
		}

		@Override
		public List<UUID> getGroupsUUID() {
			// TODO: Implement
			return null;
		}

		@Override
		public void setGroupsUUID(List<UUID> groupsUUID) {
			// TODO: Implement
		}
	}
}
