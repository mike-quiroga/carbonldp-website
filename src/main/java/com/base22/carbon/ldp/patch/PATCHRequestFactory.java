package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPResourceFactory;
import com.base22.carbon.ldp.patch.PATCHRequestClass.Properties;
import com.base22.carbon.ldp.patch.PATCHRequestClass.Resources;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class PATCHRequestFactory extends LDPResourceFactory {
	public PATCHRequestImpl create(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		if ( ! isPATCHRequest(ldpResource) ) {
			throw new FactoryException("The resource isn't a PATCHRequest object.");
		}
		return new PATCHRequestImpl(resource);
	}

	public PATCHRequestImpl create(String resourceURI, Model model) throws CarbonException {
		LDPResource ldpResource = super.create(resourceURI, model);
		if ( ! isPATCHRequest(ldpResource) ) {
			throw new FactoryException("The resource isn't an PATCHRequest object.");
		}
		return new PATCHRequestImpl(ldpResource.getResource());
	}

	public List<String> validate(PATCHRequest patchRequest) {
		List<String> violations = new ArrayList<String>();
		// TODO: Implement
		return violations;
	}

	public boolean isPATCHRequest(Resource resource) throws CarbonException {
		LDPResource ldpResource = super.create(resource);
		return isPATCHRequest(ldpResource);
	}

	public boolean isPATCHRequest(LDPResource ldpResource) {
		return ldpResource.isOfType(Resources.CLASS.getPrefixedURI().getURI());
	}

	protected class PATCHRequestImpl extends LDPResourceImpl implements PATCHRequest {

		public PATCHRequestImpl(Resource resource) {
			super(resource);
		}

		@Override
		public AddAction[] getAddActions() {
			List<AddAction> addActions = new ArrayList<AddAction>();
			Resource[] resources = this.getURIResources(Properties.ADD_ACTION.getProperty());
			AddActionFactory factory = new AddActionFactory();
			for (Resource resource : resources) {
				try {
					addActions.add(factory.create(resource));
				} catch (CarbonException ignore) {
				}
			}

			return addActions.toArray(new AddAction[addActions.size()]);
		}

		@Override
		public SetAction[] getSetActions() {
			List<SetAction> setActions = new ArrayList<SetAction>();
			Resource[] resources = this.getURIResources(Properties.SET_ACTION.getProperty());
			SetActionFactory factory = new SetActionFactory();
			for (Resource resource : resources) {
				try {
					setActions.add(factory.create(resource));
				} catch (CarbonException ignore) {
				}
			}

			return setActions.toArray(new SetAction[setActions.size()]);
		}

		@Override
		public DeleteAction[] getDeleteActions() {
			List<DeleteAction> deleteActions = new ArrayList<DeleteAction>();
			Resource[] resources = this.getURIResources(Properties.DELETE_ACTION.getProperty());
			DeleteActionFactory factory = new DeleteActionFactory();
			for (Resource resource : resources) {
				try {
					deleteActions.add(factory.create(resource));
				} catch (CarbonException ignore) {
				}
			}

			return deleteActions.toArray(new DeleteAction[deleteActions.size()]);
		}

	}
}
