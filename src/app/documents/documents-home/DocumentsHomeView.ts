import {Component, CORE_DIRECTIVES, ElementRef, Title } from 'angular2/angular2';
import {RouteConfig, RouterOutlet, RouterLink} from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import "./style.css!";


@Component( {
	selector: 'documents-list',
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ]
} )
export default class DocumentsHomeView {
	static parameters = [ [ ElementRef ], [ Title ] ];


	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Documents" );
	}


	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}