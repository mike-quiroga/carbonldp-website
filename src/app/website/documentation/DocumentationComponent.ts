import { Component } from "angular2/core";
import {RouteConfig, RouterOutlet} from "angular2/router";

import HomeView from "./home/HomeView";

import AboutCarbonLDPView from "./about-carbon-ldp/AboutCarbonLDPView";

import LinkedDataConceptsView from "./linked-data-concepts/LinkedDataConceptsView";
import LDPConceptsView from "./ldp-concepts/LDPConceptsView";
import CarbonLDPConceptsView from "./carbon-ldp-concepts/CarbonLDPConceptsView";

import RESTApiGettingStarted from "./rest-api/getting-started/GettingStartedView";
import InteractionModelsView from "./rest-api/interaction-models/InteractionModelsView";

import JavaScriptSDKGettingStartedView from "./javascript-sdk/getting-started/GettingStartedView";
import JavaScriptSDKContextsView from "./javascript-sdk/contexts/ContextsView";
import JavaScriptSDKObjectModelView from "./javascript-sdk/object-model/ObjectModelView";

@Component( {
	selector: "documents",
	template: "<router-outlet></router-outlet>",
	directives: [ RouterOutlet ],
} )
@RouteConfig( [
	{ path: "/", name: "Documentation", component: HomeView },

	{ path: "/about-carbon-ldp", as: "AboutCarbonLDP", component: AboutCarbonLDPView },

	{ path: "/linked-data-concepts", as: "LinkedDataConcepts", component: LinkedDataConceptsView },
	{ path: "/ldp-concepts", as: "LDPConcepts", component: LDPConceptsView },
	{ path: "/carbon-ldp-concepts", as: "CarbonLDPConcepts", component: CarbonLDPConceptsView },

	{ path: "/rest-api", as: "RESTApi", redirectTo: [ "./RESTApiGettingStarted" ]  },
	{ path: "/rest-api/getting-started", as: "RESTApiGettingStarted", component: RESTApiGettingStarted },
	{ path: "/rest-api/interaction-models", as: "RESTApiInteractionModels", component: InteractionModelsView },

	{ path: "/javascript-sdk", as: "JavaScriptSDK", redirectTo: [ "./JavaScriptSDKGettingStarted" ] },
	{ path: "/javascript-sdk/getting-started", as: "JavaScriptSDKGettingStarted", component: JavaScriptSDKGettingStartedView },
	{ path: "/javascript-sdk/contexts", as: "JavaScriptSDKContexts", component: JavaScriptSDKContextsView },
	{ path: "/javascript-sdk/object-model", as: "JavaScriptSDKObjectModel", component: JavaScriptSDKObjectModelView },
] )
export default class DocumentationComponent {

}
