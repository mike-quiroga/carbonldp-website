package com.base22.carbon.ldp.models;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.RDFResourceFactory.RDFResourceImpl;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class SystemRDFResourceFactory extends RDFResourceFactory {
	public SystemRDFResource create(Resource resource) throws CarbonException {
		RDFResource ldpResource = super.create(resource);
		if ( ! isSystemResource(ldpResource) ) {
			throw new CarbonException("The resource's URI doesn't match the pattern of a system resource.");
		}
		return new LDPSystemResourceImpl(ldpResource.getResource());
	}

	public SystemRDFResource create(String resourceURI, Model model) throws CarbonException {
		RDFResource ldpResource = super.create(resourceURI, model);
		if ( ! isSystemResource(ldpResource) ) {
			throw new CarbonException("The resource's URI doesn't match the pattern of a system resource.");
		}
		return new LDPSystemResourceImpl(ldpResource.getResource());
	}

	public boolean isSystemResource(RDFResource ldpResource) {
		return ldpResource.getURI().matches(".*" + Carbon.SYSTEM_RESOURCE_REGEX + ".*");
	}

	protected class LDPSystemResourceImpl extends RDFResourceImpl implements SystemRDFResource {
		protected Resource resource;

		public LDPSystemResourceImpl(Resource resource) {
			super(resource);
		}

		@Override
		public String getBaseURI() {
			return this.getURI().split(Carbon.SYSTEM_RESOURCE_REGEX)[0];
		}
	}
}
