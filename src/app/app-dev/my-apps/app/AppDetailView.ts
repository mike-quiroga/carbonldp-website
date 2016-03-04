/// <reference path="./../../../../../typings/typings.d.ts" />
import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, RouteConfig, Router, RouterOutlet, Instruction, RouteParams } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import SidebarService from "./../../components/sidebar/service/SidebarService";
import MyAppsService from "./../service/MyAppsService";
import App from "./App";

import DashboardView from "./dashboard/DashboardView";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";


import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "app-detail",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, RouterOutlet ],
	providers: [ MyAppsService ]
} )
@RouteConfig( [
	{
		path: "/",
		as: "AppDashboard",
		component: DashboardView,
		useAsDefault: true,
		data: {
			alias: "AppDashboard",
			displayName: "App Dashboard"
		}
	},
	{
		path: "/sparql-editor",
		as: "SPARQLEditor",
		component: SPARQLClientComponent,
		data: {
			alias: "SPARQLEditor",
			displayName: "SPARQL Editor"
		}
	}
] )
export default class AppDetailView {
	router:Router;
	routeParams:RouteParams;
	sidebarService:SidebarService;
	myAppsService:MyAppsService;

	element:ElementRef;
	$element:JQuery;
	app:App;
	timer:number;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, sidebarService:SidebarService, myAppsService:MyAppsService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.sidebarService = sidebarService;
		this.myAppsService = myAppsService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {
		return new Promise((resolve) => {
			let slug:string = this.routeParams.get( "slug" );
			this.myAppsService.getapp( slug ).then(
				( app ) => {
					if ( typeof app === "undefined" ) {
						console.log( "No Carbon App found" );
						this.timer = 5;
						let countDown = setInterval( ()=> {
							this.timer --;
							if ( this.timer == 0 ) {
								this.router.navigate( [ "/AppDev/MyApps/List" ] );
								clearInterval( countDown );
							}
						}, 1000 );
					} else {
						this.app = app;
						this.sidebarService.addApp( this.app );
					}
					resolve(true);

				},
				( error )=> {
					console.log( error );
				}
			);
		});
	}
}