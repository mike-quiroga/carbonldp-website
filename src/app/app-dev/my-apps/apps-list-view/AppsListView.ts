import { Component, ElementRef, Type } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router, RouteDefinition, ROUTER_DIRECTIVES, CanActivate} from "angular2/router";

import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";

import $ from 'jquery';
import 'semantic-ui/semantic';

import AppContextService from "./../../AppContextService";

import AppTileComponent from "./../app-tile/AppTileComponent";
import AppsListComponent from "../apps-list/AppsListComponent";
import App from "./../app/App";
import AppDetailView from "./../app/AppDetailView";

import template from "./template.html!";

@Component( {
	selector: "my-apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppTileComponent, AppsListComponent ],
} )
export default class AppsListView {
	router:Router;

	appContextService:AppContextService;
	apps:App[] = [];

	tileView:boolean = false;

	constructor( router:Router, appContextService:AppContextService ) {
		this.appContextService = appContextService;
		this.router = router;
	}

	toggleView():void {
		this.tileView = ! this.tileView;
	}

	routerOnActivate():void {
		this.appContextService.getAll().then(
			( appContexts:any ):void => {
				appContexts.forEach( ( appContext ) => {
					this.apps.push( <App>{
						slug: this.appContextService.getSlug( appContext ),
						app: appContext.app,
					} );
				} );
			},
			( error:any ):void => {
				console.error( error );
			}
		);
	}
}
