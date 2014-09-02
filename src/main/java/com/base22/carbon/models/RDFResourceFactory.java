package com.base22.carbon.models;

import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.LDPResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public interface RDFResourceFactory<O extends LDPResource> {
	public O create(Resource resource) throws CarbonException;

	public O create(String resourceURI, Model model) throws CarbonException;

	public List<String> validate(O toValidate);
}
