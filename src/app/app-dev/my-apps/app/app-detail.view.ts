import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig, Router, RouterOutlet, RouteParams } from "@angular/router-deprecated";

import * as CarbonApp from "carbonldp/App";

import { MyAppsSidebarService } from "./../my-apps-sidebar.service";
// import AppContextService from "./../../AppContextService";
import { AppContextService } from "carbon-panel/my-apps/app-context.service";
import * as App from "./app";

import { AppDashboardView } from "./app-dashboard.view";
import { SPARQLEditorView } from "./sparql-editor/SPARQLEditorView";
import { EditAppView } from "./edit-app/edit-app.view";
import ExplorerView from "./explorer/ExplorerView";
import ConfigurationView from "./configuration/ConfigurationView";


import template from "./app-detail.view.html!";
// TODO: Use encapsulated styles
import "./app-detail.view.css!";

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
		component: AppDashboardView,
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
	{
		path: "/edit",
		as: "Edit",
		component: EditAppView,
		data: {
			alias: "Edit",
			displayName: "Edit",
		},
	},
	{
		path: "/explore",
		as: "Explorer",
		component: ExplorerView,
		data: {
			alias: "Explorer",
			displayName: "Explorer",
		},
	},
	{
		path: "/configure",
		as: "Configuration",
		component: ConfigurationView,
		data: {
			alias: "Configuration",
			displayName: "Configuration",
		},
	},
] )
export class AppDetailView {
	app:App.Class;

	private router:Router;
	private routeParams:RouteParams;

	private myAppsSidebarService:MyAppsSidebarService;
	private appContextService:AppContextService;

	private element:ElementRef;
	private $element:JQuery;
	private timer:number;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, myAppsSidebarService:MyAppsSidebarService, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.myAppsSidebarService = myAppsSidebarService;
		this.appContextService = appContextService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {
		let slug:string = this.routeParams.get( "slug" );
		this.appContextService.get( slug ).then( ( appContext:CarbonApp.Context ):void => {
			this.app = App.Factory.createFrom( appContext );

			this.myAppsSidebarService.addApp( this.app );
			this.myAppsSidebarService.openApp( this.app );
		} ).catch( ( error:any ):void => {
			// TODO: Check error type
			console.log( error );
			this.timer = 5;
			let countDown:any = setInterval( ():void => {
				this.timer --;
				if( this.timer === 0 ) {
					this.router.navigate( [ "/AppDev/MyApps/List" ] );
					clearInterval( countDown );
				}
			}, 1000 );
		} );
	}
}

export default AppDetailView;
