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
		description: {
			name: "description",
			content: "Find all documents related to Carbon, from the basics concepts of Linked Data to the GUI.",
		}
	},
	},

	{
		path: "/about-carbon-ldp", as: "AboutCarbonLDP", component: AboutCarbonLDPView, data: {
		alias: "About",
		displayName: "About Carbon LDP",
		description: {
			name: "description",
			content: "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web.",
		}
	},
	},

	{
		path: "/essential-concepts", name: "EssentialConcepts", component: EssentialConceptsView,
		data: {
			alias: "EssentialConcepts",
			displayName: "Essential Concepts",
			description: {
				name: "description",
				content: "Basic Linked Data knowledge. Helpful documentation for a general overview of Linked Data.",
			}
		},
	},
	{
		path: "/essential-concepts/linked-data-concepts", as: "LinkedDataConcepts", component: LinkedDataConceptsView,
		data: {
			alias: "Linked Data Concepts",
			displayName: "Linked Data Concepts",
			description: {
				name: "description",
				content: "General overview of the basic idea behind Linked Data and why it can make your applications more powerful.",
			}
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
		description: {
			name: "description",
			content: "Access and manage applications and data by URIs using RESTful requests over HTTP. Configure apps, schedule server-side jobs, execute queries - everything in Carbon is RESTful.",
		}
	},
	},
	{
		path: "/rest-api/getting-started", as: "RESTApiGettingStarted", component: RESTApiGettingStarted,
		data: {
			alias: "REST Getting started",
			displayName: "Getting Started with REST API",
			description: {
				name: "description",
				content: "Guide to get you started. How to build an example application using the Carbon LDP REST API.",
			}
		},
	},
	{
		path: "/rest-api/interaction-models", as: "RESTApiInteractionModels", component: InteractionModelsView,
		data: {
			alias: "Interaction Models",
			displayName: "Interaction Models",
			description: {
				name: "description",
				content: "An explanation of the way you can interact with the resources on the Carbon Server.",
			}
		},
	},
	{
		path: "/rest-api/rest-object-model", as: "RESTApiObjectModel", component: RESTObjectModelView,
		data: {
			alias: "REST API Object Model",
			displayName: "REST API Object Model",
			description: {
				name: "description",
				content: "A summary of the various types of resources you can manage and interact with using REST.",
			}
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
			description: {
				name: "description",
				content: "The JavaScript Software Developer's Kit, available from the npm package manager, allows you to manage RDF data using familiar JavaScript and TypeScript programming techniques and tools. Build for execution within a web browser or Node.js.",
			}
		},
	},
	{
		path: "/javascript-sdk/getting-started", as: "JavaScriptSDKGettingStarted", component: JavaScriptSDKGettingStartedView,
		data: {
			alias: "GettingStartedJS",
			displayName: "Getting Started with JavaScript SDK",
			description: {
				name: "description",
				content: "Guide to install Carbon JavaScriptSDK and start creating and manipulating data with its basic methods.",
			}
		},
	},
	{
		path: "/javascript-sdk/contexts", as: "JavaScriptSDKContexts", component: JavaScriptSDKContextsView,
		data: {
			alias: "Contexts",
			displayName: "Contexts",
			description: {
				name: "description",
				content: "What is a context in Carbon JS SDK, how to declare, access and modify it.",
			}
		},
	},
	{
		path: "/javascript-sdk/object-model", as: "JavaScriptSDKObjectModel", component: JavaScriptSDKObjectModelView,
		data: {
			alias: "JavaScript SDK Object Model",
			displayName: "JavaScript SDK Object Model",
			description: {
				name: "description",
				content: "An in depth description of the Carbon Object Model.",
			}
		},
	},
	{
		path: "/javascript-sdk/object-schema", as: "JavaScriptSDKObjectSchema", component: JavaScriptSDKObjectSchemaView,
		data: {
			alias: "Object Schema",
			displayName: "Object Schema",
			description: {
				name: "description",
				content: "What is the object schema, how to define and use it.",
			}
		},
	},

] )
export class DocumentationView {

}

export default DocumentationView;
