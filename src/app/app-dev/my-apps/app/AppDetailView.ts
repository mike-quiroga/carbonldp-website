/// <reference path="./../../../../../typings/typings.d.ts" />
import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, RouteConfig, Router, RouterOutlet, Instruction, RouteParams } from "angular2/router";

import * as App from "carbon/App";

import SidebarService from "./../../components/sidebar/service/SidebarService";
import AppContextService from "./../../AppContextService";
import * as SidebarApp from "./App";

import DashboardView from "./dashboard/DashboardView";
import SPARQLEditorView from "./sparql-editor/SPARQLEditorView";


import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "app-detail",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, RouterOutlet ],
	providers: [ AppContextService, ],
} )
@RouteConfig( [
	{
		path: "/",
		as: "AppDashboard",
		component: DashboardView,
		useAsDefault: true,
		data: {
			alias: "AppDashboard",
			displayName: "App Dashboard",
		},
	},
	{
		path: "/sparql-editor",
		as: "SPARQLEditor",
		component: SPARQLEditorView,
		data: {
			alias: "SPARQLEditor",
			displayName: "SPARQL Editor",
		},
	},
] )
export default class AppDetailView {
	router:Router;
	routeParams:RouteParams;
	//app:App;
	public appContext:App.Context;
	private sidebarService:SidebarService;
	private appContextService:AppContextService;

	private element:ElementRef;
	private $element:JQuery;
	private timer:number;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, sidebarService:SidebarService, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.sidebarService = sidebarService;
		this.appContextService = appContextService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {
		let slug:string = this.routeParams.get( "slug" );
		this.appContextService.get( slug )
			.then(
				( appContext ) => {
					this.appContext = appContext;
					let app:SidebarApp = {

						name: appContext.app.name,
						created: appContext.app.created,
						modified: appContext.app.modified,
						slug: slug,
						app: appContext
					};
					this.sidebarService.addApp( app );
				} )
			.catch(
				( error )=> {
					// TODO: Check error type
					console.log( error );
					this.timer = 5;
					let countDown = setInterval( ()=> {
						this.timer --;
						if ( this.timer == 0 ) {
							this.router.navigate( [ '/AppDev/MyApps/List' ] );
							clearInterval( countDown );
						}
					}, 1000 );
				}
			);
	}
}