package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPResourceFactory;
import com.base22.carbon.ldp.patch.AddActionClass.Resources;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class AddActionFactory extends LDPResourceFactory {
	public AddAction create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isAddAction(ldpResource) ) {
			throw new FactoryException("The resource isn't a AddAction object.");
		}
		return new AddActionImpl(resource);
	}

	public AddAction create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
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

	public boolean isAddAction(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	protected class AddActionImpl extends LDPResourceImpl implements AddAction {

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
