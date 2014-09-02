package com.base22.carbon.models;

import com.base22.carbon.PrefixedURI;
import com.hp.hpl.jena.rdf.model.Resource;

public interface RDFResourceEnum {
	public PrefixedURI getPrefixedURI();

	public PrefixedURI[] getPrefixedURIs();

	public Resource getResource();

	public Resource[] getResources();
}
