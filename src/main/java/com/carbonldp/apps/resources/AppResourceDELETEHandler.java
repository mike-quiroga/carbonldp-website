package com.carbonldp.apps.resources;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;

import java.util.HashSet;
import java.util.Set;

@RequestHandler
public class AppResourceDELETEHandler extends AbstractDELETERequestHandler {
	public AppResourceDELETEHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( APIPreferences.InteractionModel.CONTAINER );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.RDF_SOURCE );
	}
}
