import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

// Components
import { WebsiteView } from "./website.view";
import { HomeView } from "./home/home.view";
// import { LoginView } from "./auth/login/login.view";
import { RegisterView } from "./register/register.view";
// import { BlogView } from "./blog/blog.view";
// import { BlogPostView } from "./blog/blog-post/blog-post.view";
import { SignupThanksView } from "./signup-thanks/signup-thanks.view";
// import { UIExamplesView } from "./ui-examples/ui-examples.view";

import { DocumentationView } from "app/website/documentation/documentation.view";
// DOCUMENTATION
import { HomeView as DocumentationHomeView } from "./documentation/home/home.view";
import { AboutCarbonLDPView } from "./documentation/about-carbon-ldp/about-carbon-ldp.view";
// Essential Concepts
import { EssentialConceptsView } from "./documentation/essential-concepts/essential-concepts.view";
import { LinkedDataConceptsView } from "./documentation/essential-concepts/linked-data-concepts.view";
// import { LDPConceptsView } from "./documentation/essential-concepts/ldp-concepts.view";
// import { CarbonLDPConceptsView } from "./documentation/essential-concepts/carbon-ldp-concepts.view";

//RESTApi
import { RESTApiView } from "./documentation/rest-api/rest-api.view";
import { GettingStartedView } from "./documentation/rest-api/getting-started.view";
import { InteractionModelsView } from "./documentation/rest-api/interaction-models.view";
import { ObjectModelView } from "./documentation/rest-api/object-model.view";
import { RDFSourceView } from "./documentation/rest-api/rdf-source.view";
import { ContainersView } from "./documentation/rest-api/containers.view";

//JavaScriptSDK
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
					alias: "Home",
					title: "Home",
				},
			},
			{
				path: "register",
				component: RegisterView,
				data: {
					alias: "Register",
					title: "Register"
				},
			},
			{
				path: "signup-thanks",
				component: SignupThanksView,
				data: {
					alias: "SignupThanks",
					title: "Thank you"
				},
			},
			{
				path: "documentation",
				component: DocumentationView,
				data: {
					alias: "Documentation",
					title: false,
				},
				children: [
					{
						path: "",
						component: DocumentationHomeView,
						data: {
							title: "Documentation"
						}
					},
					{
						path: "about-carbon-ldp",
						component: AboutCarbonLDPView,
						data: {
							alias: "About",
							title: "About Carbon LDP",
						},
					},
					{
						path: "essential-concepts",
						component: EssentialConceptsView,
						data: {
							alias: "EssentialConcepts",
							title: "Essential Concepts",
						},
					},
					{
						path: "linked-data-concepts",
						component: LinkedDataConceptsView,
						data: {
							alias: "Linked Data Concepts",
							title: "Linked Data Concepts",
						},
					},
// 					{
// 						path: "/ldp-concepts",
// 						component: LDPConceptsView,
// 						data: {
// 							alias: "LDP concepts",
// 							title: "LDP concepts",
// 						},
// 					},
// 					{
// 						path: "/carbon-ldp-concepts",
// 						component: CarbonLDPConceptsView,
// 						data: {
// 							alias: "Carbon LDP concepts",
// 							title: "Carbon LDP concepts",
// 						},
// 					},
					{
						path: "rest-api",
						component: RESTApiView,
						data: {
							alias: "REST",
							title: "REST API",
						},
					},
					{
						path: "rest-api-getting-started",
						component: GettingStartedView,
						data: {
							alias: "REST Getting started",
							title: "Getting Started with REST API",
						},
					},
					{
						path: "rest-api-interaction-models",
						component: InteractionModelsView,
						data: {
							alias: "Interaction Models",
							title: "Interaction Models",
						},
					},
					{
						path: "rest-api-object-model",
						component: ObjectModelView,
						data: {
							alias: "REST API Object Model",
							title: "REST API Object Model",
						},
					},
					// {
					// 	path: "rest-api-rdfsource",
					// 	component: RDFSourceView,
					// 	data: {
					// 		alias: "RDF Source",
					// 		title: "RDF Source",
					// 	},
					// },
					// {
					// 	path: "rest-api-containers",
					// 	component: ContainersView,
					// 	data: {
					// 		alias: "Containers",
					// 		title: "Containers",
					// 	},
					// },
					{
						path: "javascript-sdk",
						component: JavaScriptSDKView,
						data: {
							alias: "JavaScript SDK",
							title: "JavaScript SDK",
						},
					},
					{
						path: "javascript-sdk-getting-started",
						component: JavaScriptSDKGettingStartedView,
						data: {
							alias: "GettingStartedJS",
							title: "Getting Started with JavaScript SDK",
						},
					},
					{
						path: "javascript-sdk-contexts",
						component: ContextsView,
						data: {
							alias: "Contexts",
							title: "Contexts",
						},
					},
					{
						path: "javascript-sdk-object-model",
						component: JavaScriptSDKObjectModelView,
						data: {
							alias: "JavaScript SDK Object Model",
							title: "JavaScript SDK Object Model",
						},
					},
					{
						path: "javascript-sdk-object-schema",
						component: ObjectSchemaView,
						data: {
							alias: "Object Schema",
							title: "Object Schema",
						},
					}
				]
			}
		]
	}
];

export const appRoutingProviders:any[] = [
	// ActiveContextResolver,
	// AuthenticatedGuard,
	// NotAuthenticatedGuard,
];

export const routing:ModuleWithProviders = RouterModule.forChild( websiteRoutes );