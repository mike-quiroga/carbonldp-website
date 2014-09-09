package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPResourceFactory;
import com.base22.carbon.ldp.patch.SetActionClass.Resources;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class SetActionFactory extends LDPResourceFactory {
	public SetAction create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isSetAction(ldpResource) ) {
			throw new FactoryException("The resource isn't a SetAction object.");
		}
		return new SetActionImpl(resource);
	}

	public SetAction create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
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

	public boolean isSetAction(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	protected class SetActionImpl extends LDPResourceImpl implements SetAction {

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
