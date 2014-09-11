package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.RDFResource;
import com.base22.carbon.ldp.models.RDFResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class DeleteActionFactory extends RDFResourceFactory {
	public DeleteAction create(Resource resource) throws CarbonException {
		RDFResource ldpResource = super.create(resource);
		if ( ! isDeleteAction(ldpResource) ) {
			throw new FactoryException("The resource isn't a DeleteAction object.");
		}
		return new DeleteActionImpl(resource);
	}

	public DeleteAction create(String resourceURI, Model model) throws CarbonException {
		RDFResource ldpResource = super.create(resourceURI, model);
		if ( ! isDeleteAction(ldpResource) ) {
			throw new FactoryException("The resource isn't an DeleteAction object.");
		}
		return new DeleteActionImpl(ldpResource.getResource());
	}

	public List<String> validate(AddAction addAction) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isDeleteAction(RDFResource ldpResource) {
		Resource resource = ldpResource.getResource();
		if ( ! resource.isURIResource() ) {
			return false;
		}
		return resource.getURI().endsWith(DeleteActionClass.UNIQUE_SUFIX);
	}

	protected class DeleteActionImpl extends LDPResourceImpl implements DeleteAction {

		public DeleteActionImpl(Resource resource) {
			super(resource);
		}

		@Override
		public String getSubjectURI() {
			String uri = this.getURI();
			uri = uri.endsWith(DeleteActionClass.UNIQUE_SUFIX) ? uri.replace(DeleteActionClass.UNIQUE_SUFIX, "") : uri;
			return uri;
		}

	}
}
