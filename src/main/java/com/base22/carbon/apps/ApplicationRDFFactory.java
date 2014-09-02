package com.base22.carbon.apps;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.apps.Application.Properties;
import com.base22.carbon.apps.Application.Resources;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPResourceFactory;
import com.base22.carbon.models.RDFResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ApplicationRDFFactory extends LDPResourceFactory implements RDFResourceFactory<ApplicationRDF> {
	public ApplicationRDF create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isRDFApplication(ldpResource) ) {
			throw new FactoryException("The resource isn't an Application object.");
		}
		return new RDFApplicationImpl(resource);
	}

	public ApplicationRDF create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
		if ( ! isRDFApplication(ldpResource) ) {
			throw new FactoryException("The resource isn't an Application object.");
		}
		return new RDFApplicationImpl(ldpResource.getResource());
	}

	public ApplicationRDF create(Application application) {
		Model model = ModelFactory.createDefaultModel();

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(Carbon.URL)
			.append("/apps/")
			.append(application.getUuidString())
		;
		//@formatter:on

		Resource resource = model.createResource(uriBuilder.toString());

		ApplicationRDF rdfApplication = new RDFApplicationImpl(resource);
		rdfApplication.setType(Resources.CLASS.getResource());
		rdfApplication.setUUID(application.getUuid());
		rdfApplication.setSlug(application.getSlug());
		rdfApplication.setName(application.getName());
		rdfApplication.setMasterKey(application.getMasterKey());

		// TODO: Set the domains

		return rdfApplication;
	}

	public List<String> validate(ApplicationRDF rdfApplication) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isRDFApplication(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	private class RDFApplicationImpl extends LDPResourceImpl implements ApplicationRDF {
		public RDFApplicationImpl(Resource resource) {
			super(resource);
		}

		@Override
		public UUID getUUID() {
			return this.getUUIDProperty(Properties.UUID.getProperty());
		}

		@Override
		public void setUUID(UUID uuid) {
			this.setProperty(Properties.UUID.getProperty(), uuid);
		}

		@Override
		public String getSlug() {
			return this.getStringProperty(Properties.SLUG.getProperty());
		}

		@Override
		public void setSlug(String slug) {
			this.setProperty(Properties.SLUG.getProperty(), slug);
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
		public String getMasterKey() {
			return this.getStringProperty(Properties.MASTER_KEY.getProperty());
		}

		@Override
		public void setMasterKey(String masterKey) {
			this.setProperty(Properties.MASTER_KEY.getProperty(), masterKey);
		}

		@Override
		public String[] getDomains() {
			return this.getStringProperties(Properties.DOMAIN.getProperty());
		}

		@Override
		public void setDomains(String[] allowedDomains) {
			this.setProperty(Properties.DOMAIN.getProperty(), allowedDomains);
		}

	}
}
