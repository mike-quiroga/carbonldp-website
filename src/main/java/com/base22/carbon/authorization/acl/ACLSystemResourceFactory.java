package com.base22.carbon.authorization.acl;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPSystemResource;
import com.base22.carbon.ldp.models.LDPSystemResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ACLSystemResourceFactory extends LDPSystemResourceFactory {

	public ACLSystemResource create(Resource resource) throws CarbonException {
		LDPSystemResource systemResource = super.create(resource);
		if ( ! this.isACLSystemResource(systemResource) ) {
			throw new FactoryException("The resource isn't an AccessControlList object.");
		}
		return new ACLSystemResourceImpl(systemResource.getResource());
	}

	public ACLSystemResource create(String aclURI, Model model) throws CarbonException {
		LDPSystemResource systemResource = super.create(aclURI, model);
		if ( ! this.isACLSystemResource(systemResource) ) {
			throw new FactoryException("The resource isn't an AccessControlList object.");
		}
		return new ACLSystemResourceImpl(systemResource.getResource());
	}

	public ACLSystemResource create(LDPResource accessToResource) {
		ACLSystemResource aclSR = null;

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(accessToResource.getURI())
			.append(Carbon.SYSTEM_RESOURCE_SIGN)
			.append(AclSR.PREFIX)
		;
		//@formatter:on
		String uri = uriBuilder.toString();

		Resource resource = accessToResource.getResource().getModel().createResource(uri);
		aclSR = new ACLSystemResourceImpl(resource);
		aclSR.setType(AclSR.Resources.CLASS.getResource());
		aclSR.setAccessTo(accessToResource.getResource());

		accessToResource.setAclSR(aclSR);

		return aclSR;
	}

	public List<String> validate(ACLSystemResource aclSR) {
		List<String> violations = new ArrayList<String>();

		// accessTo validations
		Resource accessTo = aclSR.getAccessTo();
		if ( accessTo == null ) {
			violations.add(AclSR.Properties.ACCESS_TO.getSlug() + " > Doesn't exist or it is not valid. Should be a URI Resource.");
		}

		// ACE entries validations
		List<ACESystemResource> entries = aclSR.getACEntries();
		if ( entries.isEmpty() ) {
			violations.add(AclSR.Properties.HAS_ACE.getSlug() + " > The ACL doesn't have entries attached to it.");
		} else {
			// Validate each AceSR
			ACESystemResourceFactory aceFactory = new ACESystemResourceFactory();
			for (ACESystemResource entry : entries) {
				List<String> aceViolations = aceFactory.validate(entry);
				for (String aceViolation : aceViolations) {
					StringBuilder violationBuilder = new StringBuilder();
					violationBuilder.append(entry.getURI()).append(" - ").append(aceViolation);

					violations.add(violationBuilder.toString());
				}
			}
		}

		return violations;
	}

	public boolean isACLSystemResource(LDPSystemResource systemResource) {
		return systemResource.isOfType(AclSR.Resources.CLASS.getPrefixedURI().getURI());
	}

	protected class ACLSystemResourceImpl extends LDPSystemResourceImpl implements ACLSystemResource {

		public ACLSystemResourceImpl(Resource resource) {
			super(resource);
		}

		@Override
		public Resource getAccessTo() {
			if ( ! this.getResource().hasProperty(AclSR.Properties.ACCESS_TO.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(AclSR.Properties.ACCESS_TO.getProperty());
			if ( statement == null ) {
				return null;
			}

			try {
				return statement.getResource();
			} catch (Exception ignore) {
				return null;
			}
		}

		@Override
		public void setAccessTo(Resource accessTo) {
			if ( this.getResource().hasProperty(AclSR.Properties.ACCESS_TO.getProperty()) ) {
				this.getResource().removeAll(AclSR.Properties.ACCESS_TO.getProperty());
			}
			this.getResource().addProperty(AclSR.Properties.ACCESS_TO.getProperty(), accessTo);
		}

		@Override
		public List<ACESystemResource> getACEntries() {
			List<ACESystemResource> entries = new ArrayList<ACESystemResource>();
			if ( ! this.getResource().hasProperty(AclSR.Properties.HAS_ACE.getProperty()) ) {
				return entries;
			}

			ACESystemResourceFactory factory = new ACESystemResourceFactory();

			StmtIterator properties = this.getResource().listProperties(AclSR.Properties.HAS_ACE.getProperty());
			while (properties.hasNext()) {
				Statement statement = properties.next();
				RDFNode node = statement.getObject();
				if ( node.isURIResource() ) {
					try {
						entries.add(factory.create(node.asResource()));
					} catch (CarbonException ignore) {
					}
				}
			}
			return entries;
		}

		@Override
		public void addACEntry(ACESystemResource acEntry) {
			this.getResource().addProperty(AclSR.Properties.HAS_ACE.getProperty(), acEntry.getResource());
		}
	}
}
