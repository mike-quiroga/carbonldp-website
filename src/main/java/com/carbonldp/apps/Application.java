package com.carbonldp.apps;

import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;

import com.carbonldp.commons.apps.ApplicationDescription.Property;
import com.carbonldp.commons.apps.ApplicationDescription.SystemManagedProperty;
import com.carbonldp.commons.models.RDFResource;

public class Application extends RDFResource {

	private static final long serialVersionUID = 640544665380079388L;

	public Application(AbstractModel base, URI context) {
		super(base, context);
	}

	public String getName() {
		return this.getString(Property.NAME);
	}

	public boolean setName(String name) {
		return this.set(Property.NAME.getURI(), name);
	}

	public Set<String> getDomains() {
		return this.getStrings(Property.DOMAIN);
	}

	public boolean addDomain(String domain) {
		return this.add(Property.DOMAIN.getURI(), domain);
	}

	public String getRepositoryID() {
		return this.getString(Property.REPOSITORY_ID);
	}

	public boolean setRepositoryID(String repositoryID) {
		return this.set(Property.REPOSITORY_ID.getURI(), repositoryID);
	}

	public URI getRootContainerURI() {
		return this.getURI(Property.ROOT_CONTAINER);
	}

	public boolean setRootContainerURI(URI rootContainer) {
		return this.set(Property.ROOT_CONTAINER.getURI(), rootContainer);
	}

	@Override
	public boolean removeSystemManagedProperties() {
		boolean removedSomething = super.removeSystemManagedProperties();
		for (SystemManagedProperty property : SystemManagedProperty.values()) {
			removedSomething = this.remove(property) || removedSomething;
		}
		return removedSomething;
	}
}
