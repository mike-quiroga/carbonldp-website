import { Component, ViewEncapsulation } from "@angular/core";
import { RouteConfig, RouterOutlet } from "@angular/router-deprecated";

import HomeView from "./home/home.view";

import AboutCarbonLDPView from "./about-carbon-ldp/about-carbon-ldp.view";

import EssentialConceptsView from "./essential-concepts/essential-concepts.view";
import LinkedDataConceptsView from "./essential-concepts/linked-data-concepts.view";
import LDPConceptsView from "./essential-concepts/ldp-concepts.view";
import CarbonLDPConceptsView from "./essential-concepts/carbon-ldp-concepts.view";

import RESTApiView from "./rest-api/rest-api.view";
import RESTApiGettingStarted from "./rest-api/getting-started.view";
import InteractionModelsView from "./rest-api/interaction-models.view";
import RESTObjectModelView from "./rest-api/object-model.view";
import RESTRdfSourceView from "./rest-api/rdf-source.view";
import RESTContainersView from "./rest-api/containers.view";

import JavaScriptSDKView from "./javascript-sdk/javascript-sdk.view";
import JavaScriptSDKGettingStartedView from "./javascript-sdk/getting-started.view";
import JavaScriptSDKContextsView from "./javascript-sdk/contexts.view";
import JavaScriptSDKObjectModelView from "./javascript-sdk/object-model.view";
import JavaScriptSDKObjectSchemaView from "./javascript-sdk/object-schema.view";

import style from "./documentation.view.css!text";

@Component( {
	selector: "documents",
	template: "<router-outlet></router-outlet>",
	directives: [ RouterOutlet ],
	encapsulation: ViewEncapsulation.None,
	styles: [ style ],
} )
@RouteConfig( [
	{
		path: "/", name: "Documentation", component: HomeView, data: {
		alias: "Documentation",
		displayName: "Documentation",
	},
	},

	{
		path: "/about-carbon-ldp", as: "AboutCarbonLDP", component: AboutCarbonLDPView, data: {
		alias: "About",
		displayName: "About Carbon LDP",
	},
	},

	{
		path: "/essential-concepts", name: "EssentialConcepts", component: EssentialConceptsView,
		data: {
			alias: "EssentialConcepts",
			displayName: "Essential Concepts",
		},
	},
	{
		path: "/essential-concepts/linked-data-concepts", as: "LinkedDataConcepts", component: LinkedDataConceptsView,
		data: {
			alias: "Linked Data Concepts",
			displayName: "Linked Data Concepts",
		},
	},
	{
		path: "/essential-concepts/ldp-concepts", as: "LDPConcepts", component: LDPConceptsView,
		data: {
			alias: "LDP concepts",
			displayName: "LDP concepts",
		},
	},
	{
		path: "/essential-concepts/carbon-ldp-concepts", as: "CarbonLDPConcepts", component: CarbonLDPConceptsView, data: {
		alias: "Carbon LDP concepts",
		displayName: "Carbon LDP concepts",
	},
	},

	{
		path: "/rest-api", name: "RESTApi", component: RESTApiView, data: {
		alias: "REST",
		displayName: "REST API",
	},
	},
	{
		path: "/rest-api/getting-started", as: "RESTApiGettingStarted", component: RESTApiGettingStarted,
		data: {
			alias: "REST Getting started",
			displayName: "Getting Started with REST API",
		},
	},
	{
		path: "/rest-api/interaction-models", as: "RESTApiInteractionModels", component: InteractionModelsView,
		data: {
			alias: "Interaction Models",
			displayName: "Interaction Models",
		},
	},
	{
		path: "/rest-api/rest-object-model", as: "RESTApiObjectModel", component: RESTObjectModelView,
		data: {
			alias: "REST API Object Model",
			displayName: "REST API Object Model",
		},
	},
	{
		path: "/rest-api/rest-rdfsource", as: "RESTApiRdfSource", component: RESTRdfSourceView,
		data: {
			alias: "RDF Source",
			displayName: "RDF Source",
		},
	},
	{
		path: "/rest-api/rest-containers", as: "RESTApiContainers", component: RESTContainersView,
		data: {
			alias: "Containers",
			displayName: "Containers",
		},
	},

	{
		path: "/javascript-sdk", as: "JavaScriptSDK", component: JavaScriptSDKView,
		data: {
			alias: "JavaScript SDK",
			displayName: "JavaScript SDK",
		},
	},
	{
		path: "/javascript-sdk/getting-started", as: "JavaScriptSDKGettingStarted", component: JavaScriptSDKGettingStartedView,
		data: {
			alias: "GettingStartedJS",
			displayName: "Getting Started with JavaScript SDK",
		},
	},
	{
		path: "/javascript-sdk/contexts", as: "JavaScriptSDKContexts", component: JavaScriptSDKContextsView,
		data: {
			alias: "Contexts",
			displayName: "Contexts",
		},
	},
	{
		path: "/javascript-sdk/object-model", as: "JavaScriptSDKObjectModel", component: JavaScriptSDKObjectModelView,
		data: {
			alias: "JavaScript SDK Object Model",
			displayName: "JavaScript SDK Object Model",
		},
	},
	{
		path: "/javascript-sdk/object-schema", as: "JavaScriptSDKObjectSchema", component: JavaScriptSDKObjectSchemaView,
		data: {
			alias: "Object Schema",
			displayName: "Object Schema",
		},
	},

] )
export class DocumentationView {

}

export default DocumentationView;
