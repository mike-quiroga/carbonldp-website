import {Component, CORE_DIRECTIVES, ElementRef, Title } from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
//import "./style.css!";


@Component( {
	selector: 'carbon-ldp-concepts',
	template: template,
	directives: [ CORE_DIRECTIVES ],
	providers: [ Title ]
} )
export default class CarbonLDPConceptsView {
	static parameters = [ [ ElementRef ], [ Title ] ];


	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Carbon LDP Concepts" );
	}


	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}