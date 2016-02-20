import { Component, ElementRef, Type } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { Router, RouteDefinition, ROUTER_DIRECTIVES, RouteConfig, RouterOutlet } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import CarbonAppView from "./../carbon-app/CarbonAppView";
import CarbonAppTileComponent from './../carbon-app-tile/CarbonAppTileComponent';
import CarbonApp from "./../carbon-app/CarbonApp";
import MyAppsListView from "./../my-apps-list-view/MyAppsListView";

@Component( {
	selector: 'my-apps',
	template: '<router-outlet></router-outlet>',
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CarbonAppTileComponent, RouterOutlet ],
} )
@RouteConfig( [
	{
		path: '/',
		as: 'List',
		component: MyAppsListView,
		useAsDefault: true,
		data: {
			alias: "List",
			displayName: "My Apps"
		}
	},
	{
		path: '/:slug/...',
		as: 'CarbonApp',
		component: CarbonAppView,
		data: {
			alias: "CarbonApp",
			displayName: "Carbon App",
			params: {
				name: "slug",
				redirectTo: "AppDashboard"
			}
		}
	}
] )
export default class MyAppsView {
	static parameters = [ [ ElementRef ] ];

	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}
}