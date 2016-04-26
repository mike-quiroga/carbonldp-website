import { Component } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router, ROUTER_DIRECTIVES } from "angular2/router";

import "semantic-ui/semantic";

import AppContextService from "./../../AppContextService";

import AppTileComponent from "./../app-tile/AppTileComponent";
import App from "./../app/App";

import template from "./template.html!";

@Component( {
	selector: "my-apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppTileComponent ]
} )
export default class AppsListView {
	router:Router;

	appContextService:AppContextService;
	apps:App[] = [];
	routeDefinitions = [];

	constructor( router:Router, appContextService:AppContextService ) {
		this.appContextService = appContextService;
		this.router = router;
	}

	routerOnActivate():void {
		this.appContextService.getAll().then(
			( appContexts:any ) => {
				appContexts.forEach( ( appContext ) => {
					this.apps.push( <App>{
						slug: this.appContextService.getSlug( appContext ),
						app: appContext.app
					} );
				} );
			},
			( error )=> {
				console.error( error );
			}
		);
	}
}