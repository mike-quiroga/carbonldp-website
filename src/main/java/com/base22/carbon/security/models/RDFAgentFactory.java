package com.base22.carbon.security.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.exceptions.FactoryException;
import com.base22.carbon.models.LDPResource;
import com.base22.carbon.models.LDPResourceFactory;
import com.base22.carbon.security.models.Agent.Properties;
import com.base22.carbon.security.models.Agent.Resources;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFAgentFactory extends LDPResourceFactory {

	public RDFAgent create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! this.isRDFAgent(ldpResource) ) {
			throw new FactoryException("The resource isn't an RDFAgent object.");
		}
		return new RDFAgentImpl(ldpResource.getResource());
	}

	public RDFAgent create(String rdfAgentURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(rdfAgentURI, model);
		if ( ! this.isRDFAgent(ldpResource) ) {
			throw new FactoryException("The resource isn't an RDFAgent object.");
		}
		return new RDFAgentImpl(ldpResource.getResource());
	}

	public RDFAgent create(Agent agent) {
		Model model = ModelFactory.createDefaultModel();

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(Carbon.URL)
			.append("/agents/")
			.append(agent.getUuid())
		;
		//@formatter:on

		Resource resource = model.createResource(uriBuilder.toString());

		RDFAgent rdfAgent = new RDFAgentImpl(resource);
		rdfAgent.setType(Resources.CLASS.getResource());
		rdfAgent.setUUID(agent.getUuid());
		rdfAgent.setFullName(agent.getFullName());
		rdfAgent.setMainEmail(agent.getMainEmail());
		// The API Key and the password are not restored (not by this means...)

		return rdfAgent;
	}

	public List<String> validate(ACESystemResource aceSR) {
		List<String> violations = new ArrayList<String>();

		// TODO: Implement

		return violations;
	}

	public boolean isRDFAgent(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	protected class RDFAgentImpl extends LDPResourceImpl implements RDFAgent {
		public RDFAgentImpl(Resource resource) {
			super(resource);
		}

		@Override
		public UUID getUUID() {
			return this.getUUIDProperty(Properties.UUID.getProperty());
		}

		@Override
		public void setUUID(UUID agentUUID) {
			this.setProperty(Properties.UUID.getProperty(), agentUUID);
		}

		@Override
		public String getFullName() {
			return this.getStringProperty(Properties.FULL_NAME.getProperty());
		}

		@Override
		public void setFullName(String fullName) {
			this.setProperty(Properties.FULL_NAME.getProperty(), fullName);
		}

		@Override
		public String getMainEmail() {
			return this.getStringProperty(Properties.EMAIL.getProperty());
		}

		@Override
		public void setMainEmail(String mainEmail) {
			this.setProperty(Properties.EMAIL.getProperty(), mainEmail);
		}

		@Override
		public String getPassword() {
			return this.getStringProperty(Properties.PASSWORD.getProperty());
		}

		@Override
		public void setPassword(String password) {
			this.setProperty(Properties.PASSWORD.getProperty(), password);
		}

		@Override
		public String getAPIKey() {
			return this.getStringProperty(Properties.API_KEY.getProperty());
		}

		@Override
		public void setAPIKey(String apiKey) {
			this.setProperty(Properties.API_KEY.getProperty(), apiKey);
		}

	}
}
