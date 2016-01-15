import {Component } from 'angular2/angular2';
import {RouteConfig, RouterOutlet} from 'angular2/router';

import DocumentsHomeView from 'app/documents/documents-home/DocumentsHomeView';
import LinkedDataConceptsView from 'app/documents/linked-data-concepts/LinkedDataConceptsView';

import template from './template.html!';

@Component( {
	selector: 'documents',
	template: template,
	directives: [ RouterOutlet ]
} )
@RouteConfig( [
	{path: '/', name: 'Documents', component: DocumentsHomeView},
	{path: '/linked-data-concepts', as: 'LinkedDataConcepts', component: LinkedDataConceptsView}
] )
export default class DocumentsComponent { }