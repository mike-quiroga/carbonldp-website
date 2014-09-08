package com.base22.carbon.apps.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.AntPathMatcher;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationRDFFactory;
import com.base22.carbon.apps.roles.ApplicationRole.Properties;
import com.base22.carbon.apps.roles.ApplicationRole.Resources;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ApplicationRoleRDFFactory extends LDPResourceFactory {

	public ApplicationRoleRDF create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isRDFApplicationRole(ldpResource) ) {
			throw new FactoryException("The resource isn't an ApplicationRole object.");
		}
		return new RDFApplicationRoleImpl(resource);
	}

	public ApplicationRoleRDF create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
		if ( ! isRDFApplicationRole(ldpResource) ) {
			throw new FactoryException("The resource isn't an ApplicationRole object.");
		}
		return new RDFApplicationRoleImpl(ldpResource.getResource());
	}

	public ApplicationRoleRDF create(ApplicationRole appRole) {
		Model model = ModelFactory.createDefaultModel();

		String roleURI = composeURI(appRole.getApplicationSlug(), appRole.getSlug());

		Resource resource = model.createResource(roleURI);

		RDFApplicationRoleImpl role = new RDFApplicationRoleImpl(resource);
		role.setType(Resources.CLASS.getResource());
		role.setSlug(appRole.getSlug());
		role.setName(appRole.getName());
		role.setDescription(appRole.getDescription());

		String appURI = ApplicationRDFFactory.composeURI(appRole.getApplicationSlug());
		role.setAppURI(appURI);

		if ( appRole.getParentSlug() != null ) {
			String parentURI = composeURI(appRole.getApplicationSlug(), appRole.getParentSlug());
			role.setParentURI(parentURI);
		}

		// TODO: Set the other properties

		return role;
	}

	public List<String> validate(ApplicationRoleRDF applicationRole) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isRDFApplicationRole(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	public static String composeURI(String appSlug, String roleSlug) {
		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(Carbon.URL)
			.append(Application.ENDPOINT)
			.append(appSlug)
			.append(ApplicationRole.ENDPOINT)
			.append(roleSlug)
		;
		//@formatter:on

		return uriBuilder.toString();
	}

	private class RDFApplicationRoleImpl extends LDPResourceImpl implements ApplicationRoleRDF {
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
			return this.getString(Properties.NAME.getProperty());
		}

		@Override
		public void setName(String name) {
			this.setProperty(Properties.NAME.getProperty(), name);
		}

		@Override
		public String getDescription() {
			return this.getString(Properties.DESCRIPTION.getProperty());
		}

		@Override
		public void setDescription(String description) {
			this.setProperty(Properties.DESCRIPTION.getProperty(), description);
		}

		@Override
		public String getSlug() {
			return this.getString(Properties.SLUG.getProperty());
		}

		@Override
		public void setSlug(String slug) {
			this.setProperty(Properties.SLUG.getProperty(), slug);
		}

		@Override
		public String getAppSlug() {
			StringBuilder appSlugPattern = new StringBuilder();
			//@formatter:off
			appSlugPattern
				.append(Carbon.URL)
				.append(Application.ENDPOINT)
				.append("{appSlug}")
				.append(ApplicationRole.ENDPOINT)
				.append("**")
			;
			//@formatter:on

			AntPathMatcher matcher = new AntPathMatcher();
			Map<String, String> variables = matcher.extractUriTemplateVariables(appSlugPattern.toString(), this.getResource().getURI());

			if ( ! variables.containsKey("appSlug") ) {
				return null;
			}

			return variables.get("appSlug");
		}

		@Override
		public String getAppURI() {
			return this.getURIProperty(Properties.APPLICATION.getProperty());
		}

		@Override
		public void setAppURI(String applicationURI) {
			this.setProperty(Properties.APPLICATION.getProperty(), ResourceFactory.createResource(applicationURI));
		}

		@Override
		public String getParentURI() {
			return this.getURIProperty(Properties.PARENT.getProperty());
		}

		@Override
		public void setParentURI(String parentURI) {
			this.setProperty(Properties.PARENT.getProperty(), ResourceFactory.createResource(parentURI));
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
