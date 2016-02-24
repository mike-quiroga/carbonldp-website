import { Component, Input, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { Router, ROUTER_DIRECTIVES } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import App from "../app/App";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'app-tile',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class AppTileComponent {
	static parameters = [ [ ElementRef ], [ Router ] ];
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() app:App;

	constructor( element:ElementRef, router:Router ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		//console.log( "App :%o", this.carbonApp );
	}
}