import {Component} from "@angular/core";
import {Title} from "@angular/platform-browser";
import {RouteConfig, RouterOutlet} from "@angular/router-deprecated";

import DocumentsHomeView from "app/website/documents/documents-home/DocumentsHomeView";
import LinkedDataConceptsView from "app/website/documents/linked-data-concepts/LinkedDataConceptsView";
import AboutCarbonLDPView from "app/website/documents/about-carbon-ldp/AboutCarbonLDPView";
import CarbonLDPConceptsView from "app/website/documents/carbon-ldp-concepts/CarbonLDPConceptsView";
import GettingStartedWithTheRestApiView from "app/website/documents/getting-started-with-the-rest-api/GettingStartedWithTheRestApiView";
import RESTObjectModelView from "app/website/documents/rest-object-model/RESTObjectModelView";
import RESTdfSourceView from "app/website/documents/rest-rdfsource/RESTdfSourceView";
import RESTContainersView from "app/website/documents/rest-containers/RESTContainersView";
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
	{path: "/rest-object-model", as: "RestObjectModel", component: RESTObjectModelView},
	{path: "/rest-rdfsource", as: "RestRDFSource", component: RESTdfSourceView},
	{path: "/rest-containers", as: "RestContainers", component: RESTContainersView},
	{path: "/interaction-models", as: "InteractionModels", component: InteractionModelsView},
	{path: "/ldp-concepts", as: "LDPConcepts", component: LDPConceptsView}
] )
export default class DocumentsComponent {

}
