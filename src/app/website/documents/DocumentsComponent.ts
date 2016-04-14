import { Component } from "angular2/core";
import { Title } from "angular2/platform/browser";
import { RouteConfig, RouterOutlet } from "angular2/router";

import DocumentsHomeView from "app/website/documents/documents-home/DocumentsHomeView";
import LinkedDataConceptsView from "app/website/documents/linked-data-concepts/LinkedDataConceptsView";
import AboutCarbonLDPView from "app/website/documents/about-carbon-ldp/AboutCarbonLDPView";
import CarbonLDPConceptsView from "app/website/documents/carbon-ldp-concepts/CarbonLDPConceptsView";
import GettingStartedWithTheRestApiView from "app/website/documents/getting-started-with-the-rest-api/GettingStartedWithTheRestApiView";
import InteractionModelsView from "app/website/documents/interaction-models/InteractionModelsView";
import LDPConceptsView from "app/website/documents/ldp-concepts/LDPConceptsView";

import template from "./template.html!";

//noinspection TypeScriptValidateTypes
@Component( {
	selector: "documents",
	template: template,
	directives: [ RouterOutlet ],
	providers: [ Title ]
} )
@RouteConfig( [
	{path: "/", name: "Documents", component: DocumentsHomeView},
	{path: "/linked-data-concepts", as: "LinkedDataConcepts", component: LinkedDataConceptsView},
	{path: "/about-carbon-ldp", as: "AboutCarbonLDP", component: AboutCarbonLDPView},
	{path: "/carbon-ldp-concepts", as: "CarbonLDPConcepts", component: CarbonLDPConceptsView},
	{path: "/getting-started-rest-api", as: "GettingStartedWithTheRestApi", component: GettingStartedWithTheRestApiView},
	{path: "/interaction-models", as: "InteractionModels", component: InteractionModelsView},
	{path: "/ldp-concepts", as: "LDPConcepts", component: LDPConceptsView}
] )
export default class DocumentsComponent {
	
}
