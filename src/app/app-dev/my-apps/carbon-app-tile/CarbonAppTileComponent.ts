import { Component, Input, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { Router, ROUTER_DIRECTIVES } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import CarbonApp from "../carbon-app/CarbonApp";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'carbon-app-tile',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class CarbonAppTileComponent {
	static parameters = [ [ ElementRef ], [ Router ] ];
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() carbonApp:CarbonApp;

	constructor( element:ElementRef, router:Router ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		//console.log( "App :%o", this.carbonApp );
	}
}