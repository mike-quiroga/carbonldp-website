/// <reference path="./../../../../../typings/typings.d.ts" />
import { Component } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router, ROUTER_DIRECTIVES, CanActivate} from "angular2/router";

import { SIDEBAR_PROVIDERS } from "app/app-dev/components/sidebar/Sidebar";

import MyAppsService from "./../service/MyAppsService";
import AppTileComponent from "./../app-tile/AppTileComponent";
import App from "./../app/App";
import AppDetailView from "./../app/AppDetailView";

import template from "./template.html!";

@Component( {
	selector: "my-apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppTileComponent ],
	providers: [ MyAppsService ]
} )
export default class AppsListView {
	router:Router;

	myAppsService:MyAppsService;
	apps:App[] = [];
	routeDefinitions = [];

	constructor( myAppsService:MyAppsService, router:Router ) {
		this.myAppsService = myAppsService;
		this.router = router;
	}

	routerOnActivate():Promise<boolean> {
		return new Promise( ( resolve ) => {
			this.myAppsService.getApps().then(
				( apps )=> {
					apps.forEach( app => {
						let data:{alias:string, displayName:string} = {
							alias: app.name.replace( new RegExp( " ", "g" ), "" ),
							displayName: app.name
						};
						this.routeDefinitions.push( {
							path: "/" + app.slug,
							component: App,
							as: app.name.replace( new RegExp( " ", "g" ), "" ),
							data: data
						} );
						app[ "data" ] = data;
						this.apps.push( app );
					} );
					resolve( true );
				},
				( error )=> {
					console.error( error );
					resolve( false );
				}
			);
		} );
	}
}