package com.carbonldp.apps.web.handlers;

import java.util.HashSet;
import java.util.Set;

import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.ldp.web.AbstractPOSTRequestHandler;
import com.carbonldp.web.RequestHandler;

@RequestHandler
public class AppResourcePOSTHandler extends AbstractPOSTRequestHandler {

	public AppResourcePOSTHandler() {
		Set<InteractionModel> supportedInteractionModels = new HashSet<InteractionModel>();
		supportedInteractionModels.add(InteractionModel.RDF_SOURCE);
		supportedInteractionModels.add(InteractionModel.CONTAINER);
		setSupportedInteractionModels(supportedInteractionModels);

		setDefaultInteractionModel(InteractionModel.RDF_SOURCE);
	}

}
