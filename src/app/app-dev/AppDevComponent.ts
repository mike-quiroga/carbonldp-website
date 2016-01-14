import { Injectable } from 'angular2/angular2';
import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, Location, RouteConfig, RouterLink, Router } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import DashboardView from 'app/apps/dashboard/DashboardView';
import template from './template.html!';

@Component( {
	selector: 'app-dev',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )

@Injectable()
export default class AppDevComponent {
	static dependencies = AppDevComponent.parameters;
	static parameters = [ [ Router ], [ ElementRef ] ];

	router:Router;

	element:ElementRef;
	$element:JQuery;

	sidebar:JQuery;


	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		$( "app > header, app > footer" ).hide();
		this.sidebar = this.$element.children( ".ui.sidebar" );
		//this.sidebar.sidebar( {
		//	scrollLock: false
		//} );
		//this.sidebar.sidebar( "show" );
	}

	onActivate():void {

	}
}
