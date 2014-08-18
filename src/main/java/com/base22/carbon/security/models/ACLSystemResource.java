package com.base22.carbon.security.models;

import java.util.List;

import com.base22.carbon.models.LDPSystemResource;
import com.hp.hpl.jena.rdf.model.Resource;

public interface ACLSystemResource extends LDPSystemResource {
	public Resource getAccessTo();

	public void setAccessTo(Resource resource);

	public List<ACESystemResource> getACEntries();

	public void addACEntry(ACESystemResource acEntry);
}
