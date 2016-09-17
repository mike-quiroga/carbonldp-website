import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

// Components
import { WebsiteView } from "./website.view";
import { HomeView } from "./home/home.view";
import { RegisterView } from "./register/register.view";
import { SignupThanksView } from "./signup-thanks/signup-thanks.view";
import { UIExamplesView } from "./ui-examples/ui-examples.view";
import { DocumentationView } from "app/website/documentation/documentation.view";
// Documentation
import { HomeView as DocumentationHomeView } from "./documentation/home/home.view";
import { AboutCarbonLDPView } from "./documentation/about-carbon-ldp/about-carbon-ldp.view";
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
							title: "Documentation"
						}
					},
					{
						path: "about-carbon-ldp",
						component: AboutCarbonLDPView,
						data: {
							title: "About Carbon LDP",
						},
					},
					{
						path: "essential-concepts",
						component: EssentialConceptsView,
						data: {
							title: "Essential Concepts",
						},
					},
					{
						path: "linked-data-concepts",
						component: LinkedDataConceptsView,
						data: {
							title: "Linked Data Concepts",
						},
					},
					{
						path: "rest-api",
						component: RESTApiView,
						data: {
							title: "REST API",
						},
					},
					{
						path: "rest-api-getting-started",
						component: GettingStartedView,
						data: {
							title: "Getting Started with REST API",
						},
					},
					{
						path: "rest-api-interaction-models",
						component: InteractionModelsView,
						data: {
							title: "Interaction Models",
						},
					},
					{
						path: "rest-api-object-model",
						component: ObjectModelView,
						data: {
							title: "REST API Object Model",
						},
					},
					{
						path: "javascript-sdk",
						component: JavaScriptSDKView,
						data: {
							title: "JavaScript SDK",
						},
					},
					{
						path: "javascript-sdk-getting-started",
						component: JavaScriptSDKGettingStartedView,
						data: {
							title: "Getting Started with JavaScript SDK",
						},
					},
					{
						path: "javascript-sdk-contexts",
						component: ContextsView,
						data: {
							title: "Contexts",
						},
					},
					{
						path: "javascript-sdk-object-model",
						component: JavaScriptSDKObjectModelView,
						data: {
							title: "JavaScript SDK Object Model",
						},
					},
					{
						path: "javascript-sdk-object-schema",
						component: ObjectSchemaView,
						data: {
							title: "Object Schema",
						},
					}
				]
			}
		]
	}
];

export const routing:ModuleWithProviders = RouterModule.forChild( websiteRoutes );