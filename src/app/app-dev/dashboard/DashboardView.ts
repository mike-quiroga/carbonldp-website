import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'home',
	template: `
		<h1>This is the Dashboard</h1>
	`,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class DashboardView {
	static parameters = [ [ Router ], [ ElementRef ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );

	}

	onDeactivate():void {

	}
}