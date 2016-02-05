import { Component, ElementRef, Type } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { Router, RouteDefinition, ROUTER_DIRECTIVES } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import MyAppsService from './../service/MyAppsService';
import CarbonAppTileComponent from './../carbon-app-tile/CarbonAppTileComponent';
import CarbonApp from "./../carbon-app/CarbonApp";
import CarbonAppView from "./../carbon-app/CarbonAppView";

import template from './template.html!';

@Component( {
	selector: 'my-apps',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CarbonAppTileComponent ],
	providers: [ MyAppsService ]
} )
export default class MyAppsView {
	static parameters = [ [ ElementRef ], [ MyAppsService ], [ Router ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;

	myAppsService:MyAppsService;
	carbonApps:CarbonApp[] = [];
	routeDefinitions = [];

	constructor( element:ElementRef, myAppsService:MyAppsService, router:Router ) {
		this.element = element;
		this.myAppsService = myAppsService;
		this.router = router;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.myAppsService.getApps().then(
			( apps )=> {
				apps.forEach( app=> {
					app.data = {
						alias: app.name.replace( new RegExp( " ", "g" ), "" ),
						displayName: app.name
					};
					this.routeDefinitions.push( {
						path: '/' + app.slug,
						component: CarbonAppView,
						as: app.name.replace( new RegExp( " ", "g" ), "" ),
						data: app.data
					} );
					this.carbonApps.push( app );
				} );
			}
		);
	}

	routerOnActivate():void {

	}
}