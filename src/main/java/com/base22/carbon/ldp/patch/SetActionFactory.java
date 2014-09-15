package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.RDFResource;
import com.base22.carbon.ldp.models.RDFResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class SetActionFactory extends RDFResourceFactory {
	public SetAction create(Resource resource) throws CarbonException {
		RDFResource ldpResource = super.create(resource);
		if ( ! isSetAction(ldpResource) ) {
			throw new FactoryException("The resource isn't a SetAction object.");
		}
		return new SetActionImpl(resource);
	}

	public SetAction create(String resourceURI, Model model) throws CarbonException {
		RDFResource ldpResource = super.create(resourceURI, model);
		if ( ! isSetAction(ldpResource) ) {
			throw new FactoryException("The resource isn't an SetAction object.");
		}
		return new SetActionImpl(ldpResource.getResource());
	}

	public List<String> validate(AddAction addAction) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isSetAction(RDFResource ldpResource) {
		Resource resource = ldpResource.getResource();
		if ( ! resource.isURIResource() ) {
			return false;
		}
		return resource.getURI().endsWith(SetActionClass.UNIQUE_SUFIX);
	}

	protected class SetActionImpl extends RDFResourceImpl implements SetAction {

		public SetActionImpl(Resource resource) {
			super(resource);
		}

		@Override
		public String getSubjectURI() {
			String uri = this.getURI();
			uri = uri.endsWith(SetActionClass.UNIQUE_SUFIX) ? uri.replace(SetActionClass.UNIQUE_SUFIX, "") : uri;
			return uri;
		}

	}
}
