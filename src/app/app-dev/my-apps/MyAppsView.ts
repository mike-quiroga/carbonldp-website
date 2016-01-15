import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';


import template from './template.html!';

@Component( {
	selector: 'carbon-app',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class MyAppsView {
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

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	onActivate():void {

	}
}