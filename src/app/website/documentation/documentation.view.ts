import { Component } from "@angular/core";
import { RouteConfig, RouterOutlet } from "@angular/router-deprecated";

import HomeView from "./home/HomeView";

import AboutCarbonLDPView from "./about-carbon-ldp/AboutCarbonLDPView";

import EssentialConceptsView from "./essential-concepts/essential-concepts.view";
import LinkedDataConceptsView from "./essential-concepts/linked-data-concepts/LinkedDataConceptsView";
import LDPConceptsView from "./essential-concepts/ldp-concepts/LDPConceptsView";
import CarbonLDPConceptsView from "./essential-concepts/carbon-ldp-concepts/CarbonLDPConceptsView";

import RESTApiView from "./rest-api/rest-api.view";
import RESTApiGettingStarted from "./rest-api/getting-started/GettingStartedView";
import InteractionModelsView from "./rest-api/interaction-models/InteractionModelsView";
import RESTObjectModelView from "./rest-api/rest-object-model/RESTObjectModelView";
import RESTRdfSourceView from "./rest-api/rest-rdfsource/RESTRdfSourceView";
import RESTContainersView from "./rest-api/rest-containers/RESTContainersView";

import JavaScriptSDKView from "./javascript-sdk/javascript-sdk.view";
import JavaScriptSDKGettingStartedView from "./javascript-sdk/getting-started.view";
import JavaScriptSDKContextsView from "./javascript-sdk/contexts.view";
import JavaScriptSDKObjectModelView from "./javascript-sdk/object-model.view";
import JavaScriptSDKObjectSchemaView from "./javascript-sdk/object-schema.view";

import "./documentation.view.css!";

@Component( {
	selector: "documents",
	template: "<router-outlet></router-outlet>",
	directives: [ RouterOutlet ],
} )
@RouteConfig( [
	{ path: "/", name: "Documentation", component: HomeView },

	{ path: "/about-carbon-ldp", as: "AboutCarbonLDP", component: AboutCarbonLDPView },

	{ path: "/essential-concepts", name: "EssentialConcepts", component: EssentialConceptsView },
	{ path: "/essential-concepts/linked-data-concepts", as: "LinkedDataConcepts", component: LinkedDataConceptsView },
	{ path: "/essential-concepts/ldp-concepts", as: "LDPConcepts", component: LDPConceptsView },
	{ path: "/essential-concepts/carbon-ldp-concepts", as: "CarbonLDPConcepts", component: CarbonLDPConceptsView },

	{ path: "/rest-api", name: "RESTApi", component: RESTApiView }, ,
	{ path: "/rest-api/getting-started", as: "RESTApiGettingStarted", component: RESTApiGettingStarted },
	{ path: "/rest-api/interaction-models", as: "RESTApiInteractionModels", component: InteractionModelsView },
	{ path: "/rest-api/rest-object-model", as: "RESTApiObjectModel", component: RESTObjectModelView },
	{ path: "/rest-api/rest-rdfsource", as: "RESTApiRdfSource", component: RESTRdfSourceView },
	{ path: "/rest-api/rest-containers", as: "RESTApiContainers", component: RESTContainersView },

	{ path: "/javascript-sdk", as: "JavaScriptSDK", component: JavaScriptSDKView },
	{ path: "/javascript-sdk/getting-started", as: "JavaScriptSDKGettingStarted", component: JavaScriptSDKGettingStartedView },
	{ path: "/javascript-sdk/contexts", as: "JavaScriptSDKContexts", component: JavaScriptSDKContextsView },
	{ path: "/javascript-sdk/object-model", as: "JavaScriptSDKObjectModel", component: JavaScriptSDKObjectModelView },
	{ path: "/javascript-sdk/object-schema", as: "JavaScriptSDKObjectSchema", component: JavaScriptSDKObjectSchemaView },

] )
export class DocumentationView {

}

export default DocumentationView;
