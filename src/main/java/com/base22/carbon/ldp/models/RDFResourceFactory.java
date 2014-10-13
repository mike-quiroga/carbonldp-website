package com.base22.carbon.ldp.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.authorization.acl.ACLSystemResource;
import com.base22.carbon.authorization.acl.ACLSystemResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFResourceFactory {

	protected final Logger LOG;

	public RDFResourceFactory() {
		this.LOG = LoggerFactory.getLogger(this.getClass());
	}

	public RDFResource create(Resource resource) throws CarbonException {
		if ( ! resource.isURIResource() ) {
			throw new CarbonException("The resource isn't a URI Resource.");
		}
		return new RDFResourceImpl(resource);
	}

	public RDFResource create(String resourceURI, Model model) throws CarbonException {
		// Try to fetch the resource that matches the URI from the model provided
		Resource resource = null;
		try {
			resource = model.getResource(resourceURI);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx create() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< create() > The resource '{}', couldn't be retrieved from the model.", resourceURI);
			}
			throw new FactoryException("The resource couldn't be retrieved from the model.");
		}

		if ( resource == null ) {
			throw new FactoryException("The resource couldn't be retrieved from the model.");
		}

		return new RDFResourceImpl(resource);
	}

	public RDFResource create(String uri) {
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(uri);

		return new RDFResourceImpl(resource);
	}

	protected class RDFResourceImpl implements RDFResource {

		protected Resource resource;

		public RDFResourceImpl(Resource resource) {
			this.resource = resource;
		}

		@Override
		public Integer getInteger(Property property) {
			if ( ! this.getResource().hasProperty(property) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(property);
			if ( statement == null ) {
				return null;
			}

			try {
				return statement.getInt();
			} catch (Exception ignore) {
				return null;
			}
		}

		@Override
		public Integer[] getIntegers(Property property) {
			List<Integer> ints = new ArrayList<Integer>();
			if ( ! this.getResource().hasProperty(property) ) {
				return ints.toArray(new Integer[ints.size()]);
			}

			StmtIterator iterator = this.getResource().listProperties(property);

			while (iterator.hasNext()) {
				Statement statement = iterator.next();

				try {
					ints.add(statement.getInt());
				} catch (Exception ignore) {
				}
			}

			return ints.toArray(new Integer[ints.size()]);
		}

		@Override
		public String getString(Property property) {
			if ( ! this.getResource().hasProperty(property) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(property);
			if ( statement == null ) {
				return null;
			}

			try {
				return statement.getString();
			} catch (Exception ignore) {
				return null;
			}
		}

		@Override
		public String[] getStrings(Property property) {
			List<String> strings = new ArrayList<String>();
			if ( ! this.getResource().hasProperty(property) ) {
				return strings.toArray(new String[strings.size()]);
			}

			StmtIterator iterator = this.getResource().listProperties(property);

			while (iterator.hasNext()) {
				Statement statement = iterator.next();

				try {
					strings.add(statement.getString());
				} catch (Exception ignore) {
				}
			}

			return strings.toArray(new String[strings.size()]);
		}

		@Override
		public UUID getUUIDProperty(Property property) {
			if ( ! this.getResource().hasProperty(property) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(property);
			if ( statement == null ) {
				return null;
			}

			String uuidString = null;
			try {
				uuidString = statement.getString();
			} catch (Exception ignore) {
				return null;
			}

			if ( ! AuthenticationUtil.isUUIDString(uuidString) ) {
				return null;
			}
			return AuthenticationUtil.restoreUUID(uuidString);
		}

		@Override
		public Resource getURIResource(Property property) {
			if ( ! this.getResource().hasProperty(property) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(property);
			if ( statement == null ) {
				return null;
			}

			RDFNode node = statement.getObject();
			if ( ! node.isURIResource() ) {
				return null;
			}
			return node.asResource();
		}

		@Override
		public Resource[] getURIResources(Property property) {
			List<Resource> resources = new ArrayList<Resource>();
			if ( ! this.getResource().hasProperty(property) ) {
				return resources.toArray(new Resource[resources.size()]);
			}

			StmtIterator iterator = this.getResource().listProperties(property);

			while (iterator.hasNext()) {
				Statement statement = iterator.next();

				RDFNode node = statement.getObject();
				if ( node.isURIResource() ) {
					resources.add(node.asResource());
				}
			}

			return resources.toArray(new Resource[resources.size()]);
		}

		@Override
		public String getURIProperty(Property property) {
			Resource resource = this.getURIResource(property);
			if ( resource == null ) {
				return null;
			}
			return resource.getURI();
		}

		@Override
		public String[] getURIProperties(Property property) {
			List<String> strings = new ArrayList<String>();
			Resource[] resources = this.getURIResources(property);
			for (Resource resource : resources) {
				strings.add(resource.getURI());
			}
			return strings.toArray(new String[strings.size()]);
		}

		@Override
		public void addProperty(Property property, int value) {
			this.getResource().addLiteral(property, value);
		}

		@Override
		public void addProperty(Property property, int[] values) {
			for (int value : values) {
				this.getResource().addLiteral(property, value);
			}
		}

		@Override
		public void addProperty(Property property, String value) {
			if ( value != null ) {
				this.getResource().addProperty(property, value);
			}
		}

		@Override
		public void addProperty(Property property, String[] values) {
			if ( values != null ) {
				for (String value : values) {
					this.getResource().addProperty(property, value);
				}
			}
		}

		@Override
		public void addProperty(Property property, UUID value) {
			// TODO: Implement
		}

		public void addProperty(Property property, Resource resource) {
			this.getResource().addProperty(property, resource);
		}

		@Override
		public void setProperty(Property property, int value) {
			this.removeProperty(property);
			this.addProperty(property, value);
		}

		@Override
		public void setProperty(Property property, int[] values) {
			this.removeProperty(property);
			this.addProperty(property, values);
		}

		@Override
		public void setProperty(Property property, String value) {
			this.removeProperty(property);
			this.addProperty(property, value);
		}

		@Override
		public void setProperty(Property property, String[] values) {
			this.removeProperty(property);
			this.addProperty(property, values);
		}

		@Override
		public void setProperty(Property property, UUID value) {
			this.removeProperty(property);

			String stringValue = null;
			if ( value != null ) {
				stringValue = value.toString();
			}
			this.setProperty(property, stringValue);
		}

		public void setProperty(Property property, Resource resource) {
			this.removeProperty(property);
			this.addProperty(property, resource);
		}

		@Override
		public void removeProperty(Property property) {
			if ( this.getResource().hasProperty(property) ) {
				this.getResource().removeAll(property);
			}
		}

		@Override
		public boolean isOfType(String typeString) {
			Resource type = ResourceFactory.createResource(typeString);
			return isOfType(type);
		}

		@Override
		public boolean isOfType(Resource type) {
			return resource.hasProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty(), type);
		}

		@Override
		public Set<String> getTypes() {
			Set<String> types = new HashSet<String>();

			StmtIterator iterator = this.getResource().listProperties(RDFResourceClass.Properties.RDF_TYPE.getProperty());

			while (iterator.hasNext()) {
				Statement statement = iterator.next();
				RDFNode node = statement.getObject();
				if ( node.isURIResource() ) {
					types.add(node.asResource().getURI());
				} else if ( node.isLiteral() ) {
					String nodeString = null;
					try {
						nodeString = node.asLiteral().getString();
					} catch (Exception e) {
					}
					if ( nodeString != null ) {
						types.add(nodeString);
					}
				}
			}

			return types;
		}

		@Override
		public void addType(Resource type) {
			this.getResource().addProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty(), type);
		}

		@Override
		public void setType(Resource type) {
			if ( this.getResource().hasProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty()) ) {
				this.getResource().removeAll(RDFResourceClass.Properties.RDF_TYPE.getProperty());
			}
			this.getResource().addProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty(), type);
		}

		@Override
		public void setTypes(List<Resource> types) {
			if ( this.getResource().hasProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty()) ) {
				this.getResource().removeAll(RDFResourceClass.Properties.RDF_TYPE.getProperty());
			}
			for (Resource type : types) {
				this.addType(type);
			}
		}

		@Override
		public void removeType(Resource type) {
			// TODO
		}

		@Override
		public void removeTypes(List<Resource> types) {
			// TODO
		}

		@Override
		public List<String> getLinkTypes() {
			List<String> types = new ArrayList<String>();
			types.add(RDFResourceClass.Resources.CLASS.getType());
			return types;
		}

		@Override
		public Resource getResource() {
			return this.resource;
		}

		@Override
		public String getURI() {
			return this.getResource().getURI();
		}

		@Override
		public String getSlug() {
			String uri = getURI();
			uri = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
			return uri.substring(uri.lastIndexOf("/") + 1);
		}

		@Override
		public ACLSystemResource getAclSR() {
			if ( ! this.getResource().hasProperty(RDFResourceClass.Properties.HAS_ACL.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(RDFResourceClass.Properties.HAS_ACL.getProperty());
			if ( statement == null ) {
				return null;
			}

			RDFNode node = statement.getObject();
			if ( ! node.isURIResource() ) {
				return null;
			}

			ACLSystemResourceFactory factory = new ACLSystemResourceFactory();
			try {
				return factory.create(node.asResource());
			} catch (CarbonException ignore) {
				return null;
			}
		}

		@Override
		public void setAclSR(ACLSystemResource aclSR) {
			if ( this.getResource().hasProperty(RDFResourceClass.Properties.HAS_ACL.getProperty()) ) {
				this.getResource().removeAll(RDFResourceClass.Properties.HAS_ACL.getProperty());
			}
			this.getResource().addProperty(RDFResourceClass.Properties.HAS_ACL.getProperty(), aclSR.getResource());
		}
	}
}
