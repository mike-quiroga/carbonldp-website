import { Component, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';


import template from './template.html!';

@Component( {
	selector: 'carbon-app',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class CarbonAppView {
	static parameters = [ [ Router ], [ ElementRef ], [ RouteParams ] ];

	router:Router;
	routeParams:RouteParams;

	element:ElementRef;
	$element:JQuery;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;

	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {

	}
}