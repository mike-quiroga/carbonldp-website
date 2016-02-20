import { Component, ElementRef, Type } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { Router, RouteDefinition, ROUTER_DIRECTIVES} from 'angular2/router';

import * as App from "carbon/App";
import * as HTTP from "carbon/HTTP";

import $ from 'jquery';
import 'semantic-ui/semantic';

import AppContextService from "./../../AppContextService";

import CarbonAppTileComponent from './../carbon-app-tile/CarbonAppTileComponent';
import CarbonApp from "./../carbon-app/CarbonApp";
import CarbonAppView from "./../carbon-app/CarbonAppView";

import template from './template.html!';

@Component( {
	selector: 'my-apps-list',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CarbonAppTileComponent ],
} )
export default class MyAppsListView {
	static parameters = [ [ ElementRef ], [ Router ], [ AppContextService ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;
	appContextService:AppContextService;

	carbonApps:CarbonApp[] = [];

	constructor( element:ElementRef, router:Router, appContextService:AppContextService ) {
		this.element = element;
		this.router = router;
		this.appContextService = appContextService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );

		this.appContextService.getAll().then(
			( appContexts:App.Context[] ) => {
				appContexts.forEach( ( appContext ) => this.carbonApps.push( {
					slug: this.appContextService.getSlug( appContext ),
					app: appContext.app
				}) );
			}
		);
	}

	routerOnActivate():void {

	}
}