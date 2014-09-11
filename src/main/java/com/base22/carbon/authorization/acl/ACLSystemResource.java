package com.base22.carbon.authorization.acl;

import java.util.List;

import com.base22.carbon.ldp.models.SystemRDFResource;
import com.hp.hpl.jena.rdf.model.Resource;

public interface ACLSystemResource extends SystemRDFResource {
	public Resource getAccessTo();

	public void setAccessTo(Resource resource);

	public List<ACESystemResource> getACEntries();

	public void addACEntry(ACESystemResource acEntry);
}
