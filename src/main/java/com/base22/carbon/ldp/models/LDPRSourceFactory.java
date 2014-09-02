package com.base22.carbon.ldp.models;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.LDPRS;
import com.base22.carbon.ldp.models.LDPResourceFactory.LDPResourceImpl;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class LDPRSourceFactory extends LDPResourceFactory {

	public LDPRSource create(Resource resource) throws CarbonException {
		if ( ! resource.isURIResource() ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< create() > The resource is not a named resource.");
			}
			throw new FactoryException("The resource is not a named resource.");
		}
		return new LDPRSourceImpl(resource);
	}

	// TODO: Use the super class create method and add proper validation
	public LDPRSource create(String resourceURI, Model resourceModel) throws CarbonException {
		// Try to fetch the resource that matches the URI from the model provided
		Resource resource = null;
		try {
			resource = resourceModel.getResource(resourceURI);
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

		// Check if the resource is tied to a property
		// (a hack to know if it already existed or was created when fetching for it)
		StmtIterator iterator = resource.listProperties();
		if ( ! iterator.hasNext() ) {
			throw new FactoryException("The resource wasn't found on the model.");
		}

		return new LDPRSourceImpl(resource);
	}

	public LDPRSource asLDPRSource(LDPRSource original) throws CarbonException {
		return this.create(original.getResource());
	}

	protected class LDPRSourceImpl extends LDPResourceImpl implements LDPRSource {

		public LDPRSourceImpl(Resource resource) {
			super(resource);
		}

		public DateTime getCreated() {
			DateTime created = null;
			Statement statement = this.resource.getProperty(LDPRS.CREATED_P);
			if ( statement == null ) {
				return created;
			}
			try {
				created = DateTime.parse(statement.getString());
			} catch (Exception exception) {

			}
			return created;

		}

		public DateTime getModified() {
			DateTime modified = null;
			Statement statement = this.resource.getProperty(LDPRS.MODIFIED_P);
			if ( statement == null ) {
				return modified;
			}
			try {
				modified = DateTime.parse(statement.getString());
			} catch (Exception exception) {

			}
			return modified;
		}

		public String getETag() {
			String eTag = null;
			DateTime modified = this.getModified();
			if ( modified == null )
				return null;

			DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
			eTag = dateTimeFormatter.print(modified);

			return eTag;
		}

		@Override
		public List<String> getLinkTypes() {
			List<String> types = super.getLinkTypes();
			types.add(LDPRS.LINK_TYPE);
			return types;
		}

		@Override
		public boolean hasExtendingResource(String resourceSlug) {
			// TODO: Implement
			return false;
		}

		@Override
		public Resource getExtendingResource(String resourceSlug) {
			// TODO: Implement
			return null;
		}

		@Override
		public List<Resource> getExtendingResources() {
			// TODO: Implement
			return null;
		}

		@Override
		public boolean hasSystemResource(String resourceSlug) {
			// TODO: Implement
			return false;
		}

		@Override
		public Resource getSystemResource(String resourceSlug) {
			// TODO: Implement
			return null;
		}

		@Override
		public List<Resource> getSystemResources() {
			// TODO: Implement
			return null;
		}

		@Override
		public Resource createSystemResource(String resourceSlug) throws CarbonException {
			Resource systemResource = null;
			Model sourceModel = this.getResource().getModel();

			StringBuilder systemResourceURIBuilder = new StringBuilder();
			//@formatter:off
			systemResourceURIBuilder
				.append(this.getURI())
				.append(Carbon.SYSTEM_RESOURCE_SIGN)
				.append(resourceSlug)
			;
			//@formatter:on
			String systemResourceURI = systemResourceURIBuilder.toString();

			try {
				systemResource = sourceModel.createResource(systemResourceURI);
			} catch (Exception e) {
				throw new CarbonException("The systemResource with URI: '{}', couldn't be created.", systemResourceURI);
			}

			return systemResource;
		}

		public void setTimestamps() {
			DateTime now = DateTime.now();

			setTimestamps(now, now);
		}

		public void setTimestamps(DateTime createdDate) {
			DateTime now = DateTime.now();

			setTimestamps(createdDate, now);
		}

		public void setTimestamps(DateTime createdDate, DateTime modifiedDate) {
			if ( this.resource.hasProperty(LDPRS.CREATED_P) ) {
				this.resource.removeAll(LDPRS.CREATED_P);
			}
			if ( this.resource.hasProperty(LDPRS.MODIFIED_P) ) {
				this.resource.removeAll(LDPRS.MODIFIED_P);
			}

			this.resource.addLiteral(LDPRS.CREATED_P, createdDate.toString());
			this.resource.addLiteral(LDPRS.MODIFIED_P, modifiedDate.toString());
		}

		public void touch() {
			DateTime now = DateTime.now();
			setTimestamps(getCreated(), now);
		}
	}
}
