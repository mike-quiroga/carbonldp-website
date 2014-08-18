package com.base22.carbon.models;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.base22.carbon.security.models.ACLSystemResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public interface LDPResource {
	public Resource getResource();

	public String getURI();

	public String getSlug();

	public String getStringProperty(Property property);

	public String[] getStringProperties(Property property);

	public UUID getUUIDProperty(Property property);

	public void setProperty(Property property, String value);

	public void setProperty(Property property, String[] value);

	public void setProperty(Property property, UUID value);

	public List<String> getLinkTypes();

	public boolean isOfType(String typeString);

	public Set<String> getTypes();

	public void addType(Resource type);

	public void setType(Resource type);

	public void setTypes(List<Resource> types);

	public void removeType(Resource type);

	public void removeTypes(List<Resource> types);

	public ACLSystemResource getAclSR();

	public void setAclSR(ACLSystemResource aclSR);
}
