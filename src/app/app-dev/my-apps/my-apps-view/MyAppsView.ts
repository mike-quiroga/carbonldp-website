import { Component, ElementRef, Type } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { Router, RouteDefinition, ROUTER_DIRECTIVES, RouteConfig, RouterOutlet } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import MyAppsService from './../service/MyAppsService';
import CarbonAppView from "./../carbon-app/CarbonAppView";
import CarbonAppTileComponent from './../carbon-app-tile/CarbonAppTileComponent';
import CarbonApp from "./../carbon-app/CarbonApp";
import MyAppsListView from "./../my-apps-list-view/MyAppsListView";

@Component( {
	selector: 'my-apps',
	template: '<router-outlet></router-outlet>',
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CarbonAppTileComponent, RouterOutlet ],
	providers: [ MyAppsService ]
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
			},
			( error )=> {
				console.error( "An error ocurred: %o", error );
			}
		);
	}

	routerOnActivate():void {

	}
}