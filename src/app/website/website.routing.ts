import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

// Components
import { WebsiteView } from "./website.view";
import { HomeView } from "./home/home.view";
import { RegisterView } from "./register/register.view";
import { SignupThanksView } from "./signup-thanks/signup-thanks.view";
import { UIExamplesView } from "./ui-examples/ui-examples.view";
import { LicenseView } from './license/license.view'
import { DocumentationView } from "app/website/documentation/documentation.view";
import { AboutCarbonLDPView } from "./about/about-carbon-ldp.view";

// Documentation
import { HomeView as DocumentationHomeView } from "./documentation/home/home.view";

// Documentation -> Essential Concepts
import { EssentialConceptsView } from "./documentation/essential-concepts/essential-concepts.view";
import { LinkedDataConceptsView } from "./documentation/essential-concepts/linked-data-concepts.view";
// Documentation -> RESTApi
import { RESTApiView } from "./documentation/rest-api/rest-api.view";
import { GettingStartedView } from "./documentation/rest-api/getting-started.view";
import { InteractionModelsView } from "./documentation/rest-api/interaction-models.view";
import { ObjectModelView } from "./documentation/rest-api/object-model.view";
// import { RDFSourceView } from "./documentation/rest-api/rdf-source.view";
// import { ContainersView } from "./documentation/rest-api/containers.view";
// Documentation -> JavaScriptSDK
import { JavaScriptSDKView } from "./documentation/javascript-sdk/javascript-sdk.view";
import { GettingStartedView as JavaScriptSDKGettingStartedView } from "./documentation/javascript-sdk/getting-started.view";
import { ContextsView } from "./documentation/javascript-sdk/contexts.view";
import { ObjectModelView as JavaScriptSDKObjectModelView } from "./documentation/javascript-sdk/object-model.view";
import { ObjectSchemaView } from "./documentation/javascript-sdk/object-schema.view";


const websiteRoutes:Routes = [
	{
		path: "",
		component: WebsiteView,
		data: {
			title: false,
		},
		children: [
			{
				path: "",
				component: HomeView,
				data: {
					title: "Home",
					description: {
						name: "description",
						content: "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web.",
					},
				},
			},
			{
				path: "about",
				component: AboutCarbonLDPView,
				data: {
					title: "About",
					description: {
						name: "description",
						content: "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web.",
					}
				},
			},
			{
				path: "register",
				component: RegisterView,
				data: {
					title: "Register"
				},
			},
			{
				path: "signup-thanks",
				component: SignupThanksView,
				data: {
					title: "Thank you"
				},
			},
			{
				path: "ui-examples",
				component: UIExamplesView,
				data: {
					title: "UI Examples"
				},
			},
			{
				path: "license",
				component: LicenseView,
				data: {
					title: "License"
				},
			},
			{
				path: "documentation",
				component: DocumentationView,
				data: {
					title: false,
				},
				children: [
					{
						path: "",
						component: DocumentationHomeView,
						data: {
							title: "Documentation",
							description: {
								name: "description",
								content: "Find all documents related to Carbon, from the basics concepts of Linked Data to the GUI.",
							}
						}
					},
					{
						path: "essential-concepts",
						component: EssentialConceptsView,
						data: {
							title: "Essential Concepts",
							description: {
								name: "description",
								content: "Basic Linked Data knowledge. Helpful documentation for a general overview of Linked Data.",
							}
						},
					},
					{
						path: "linked-data-concepts",
						component: LinkedDataConceptsView,
						data: {
							title: "Linked Data Concepts",
							description: {
								name: "description",
								content: "General overview of the basic idea behind Linked Data and why it can make your applications more powerful.",
							}
						},
					},
					{
						path: "rest-api",
						component: RESTApiView,
						data: {
							title: "REST API",
							description: {
								name: "description",
								content: "Access and manage applications and data by URIs using RESTful requests over HTTP. Configure apps, schedule server-side jobs, execute queries - everything in Carbon is RESTful.",
							}
						},
					},
					{
						path: "rest-api-getting-started",
						component: GettingStartedView,
						data: {
							title: "Getting Started with REST API",
							description: {
								name: "description",
								content: "Guide to get you started. How to build an example application using the Carbon LDP REST API.",
							}
						},
					},
					{
						path: "rest-api-interaction-models",
						component: InteractionModelsView,
						data: {
							title: "Interaction Models",
							description: {
								name: "description",
								content: "An explanation of the way you can interact with the resources on the Carbon Server.",
							}
						},
					},
					{
						path: "rest-api-object-model",
						component: ObjectModelView,
						data: {
							title: "REST API Object Model",
							description: {
								name: "description",
								content: "A summary of the various types of resources you can manage and interact with using REST.",
							}
						},
					},
					{
						path: "javascript-sdk",
						component: JavaScriptSDKView,
						data: {
							title: "JavaScript SDK",
							description: {
								name: "description",
								content: "The JavaScript Software Developer's Kit, available from the npm package manager, allows you to manage RDF data using familiar JavaScript and TypeScript programming techniques and tools. Build for execution within a web browser or Node.js.",
							}
						},
					},
					{
						path: "javascript-sdk-getting-started",
						component: JavaScriptSDKGettingStartedView,
						data: {
							title: "Getting Started with JavaScript SDK",
							description: {
								name: "description",
								content: "Guide to install Carbon JavaScriptSDK and start creating and manipulating data with its basic methods.",
							}
						},
					},
					{
						path: "javascript-sdk-contexts",
						component: ContextsView,
						data: {
							title: "Contexts",
							description: {
								name: "description",
								content: "What is a context in Carbon JS SDK, how to declare, access and modify it.",
							}
						},
					},
					{
						path: "javascript-sdk-object-model",
						component: JavaScriptSDKObjectModelView,
						data: {
							title: "JavaScript SDK Object Model",
							description: {
								name: "description",
								content: "An in depth description of the Carbon Object Model.",
							}
						},
					},
					{
						path: "javascript-sdk-object-schema",
						component: ObjectSchemaView,
						data: {
							title: "Object Schema",
							description: {
								name: "description",
								content: "What is the object schema, how to define and use it.",
							}
						},
					}
				]
			}
		]
	}
];

export const routing:ModuleWithProviders = RouterModule.forChild( websiteRoutes );