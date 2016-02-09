import {Component } from 'angular2/angular2';
import {RouteConfig, RouterOutlet} from 'angular2/router';

import DocumentsHomeView from 'app/documents/documents-home/DocumentsHomeView';
import LinkedDataConceptsView from 'app/documents/linked-data-concepts/LinkedDataConceptsView';
import AboutCarbonLDPView from 'app/documents/about-carbon-ldp/AboutCarbonLDPView';
import CarbonLDPConceptsView from 'app/documents/carbon-ldp-concepts/CarbonLDPConceptsView';
import GettingStartedWithTheRestApiView from 'app/documents/getting-started-with-the-rest-api/GettingStartedWithTheRestApiView';
import InteractionModelsView from 'app/documents/interaction-models/InteractionModelsView';
import LDPConceptsView from 'app/documents/ldp-concepts/LDPConceptsView';

import template from './template.html!';

@Component( {
	selector: 'documents',
	template: template,
	directives: [ RouterOutlet ]
} )
@RouteConfig( [
	{path: '/', name: 'Documents', component: DocumentsHomeView},
	{path: '/linked-data-concepts', as: 'LinkedDataConcepts', component: LinkedDataConceptsView},
	{path: '/about-carbon-ldp', as: 'AboutCarbonLDP', component: AboutCarbonLDPView},
	{path: '/carbon-ldp-concepts', as: 'CarbonLDPConcepts', component: CarbonLDPConceptsView},
	{path: '/getting-started-rest-api', as: 'GettingStartedWithTheRestApi', component: GettingStartedWithTheRestApiView},
	{path: '/interaction-models', as: 'InteractionModels', component: InteractionModelsView},
	{path: '/ldp-concepts', as: 'LDPConcepts', component: LDPConceptsView}
] )
export default class DocumentsComponent { }