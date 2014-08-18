package com.base22.carbon.models;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.exceptions.CarbonException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class LDPSystemResourceFactory extends LDPResourceFactory {
	public LDPSystemResource create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isSystemResource(ldpResource) ) {
			throw new CarbonException("The resource's URI doesn't match the pattern of a system resource.");
		}
		return new LDPSystemResourceImpl(ldpResource.getResource());
	}

	public LDPSystemResource create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
		if ( ! isSystemResource(ldpResource) ) {
			throw new CarbonException("The resource's URI doesn't match the pattern of a system resource.");
		}
		return new LDPSystemResourceImpl(ldpResource.getResource());
	}

	public boolean isSystemResource(LDPResource ldpResource) {
		return ldpResource.getURI().matches(".*" + Carbon.SYSTEM_RESOURCE_REGEX + ".*");
	}

	protected class LDPSystemResourceImpl extends LDPResourceImpl implements LDPSystemResource {
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
