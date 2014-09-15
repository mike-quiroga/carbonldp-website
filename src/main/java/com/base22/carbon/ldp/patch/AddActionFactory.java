package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.RDFResource;
import com.base22.carbon.ldp.models.RDFResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class AddActionFactory extends RDFResourceFactory {
	public AddAction create(Resource resource) throws CarbonException {
		RDFResource ldpResource = super.create(resource);
		if ( ! isAddAction(ldpResource) ) {
			throw new FactoryException("The resource isn't a AddAction object.");
		}
		return new AddActionImpl(resource);
	}

	public AddAction create(String resourceURI, Model model) throws CarbonException {
		RDFResource ldpResource = super.create(resourceURI, model);
		if ( ! isAddAction(ldpResource) ) {
			throw new FactoryException("The resource isn't an AddAction object.");
		}
		return new AddActionImpl(ldpResource.getResource());
	}

	public List<String> validate(AddAction addAction) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isAddAction(RDFResource ldpResource) {
		Resource resource = ldpResource.getResource();
		if ( ! resource.isURIResource() ) {
			return false;
		}
		return resource.getURI().endsWith(AddActionClass.UNIQUE_SUFIX);
	}

	protected class AddActionImpl extends RDFResourceImpl implements AddAction {

		public AddActionImpl(Resource resource) {
			super(resource);
		}

		@Override
		public String getSubjectURI() {
			String uri = this.getURI();
			uri = uri.endsWith(AddActionClass.UNIQUE_SUFIX) ? uri.replace(AddActionClass.UNIQUE_SUFIX, "") : uri;
			return uri;
		}

	}
}
