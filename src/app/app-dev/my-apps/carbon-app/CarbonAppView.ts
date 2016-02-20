import { Component, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, RouteConfig, Router, RouterOutlet, Instruction, RouteParams } from 'angular2/router';

import * as App from "carbon/App";

import $ from 'jquery';
import 'semantic-ui/semantic';

import SidebarService from "./../../components/sidebar/service/SidebarService";
import AppContextService from "./../../AppContextService";
import CarbonApp from "./CarbonApp";

import DashboardView from './dashboard/DashboardView';
import SPARQLClientComponent from './../../../sparql-client/SPARQLClientComponent';


import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'carbon-app',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, RouterOutlet ],
	providers: [ AppContextService ]
} )
@RouteConfig( [
	{
		path: '/',
		as: 'AppDashboard',
		component: DashboardView,
		useAsDefault: true,
		data: {
			alias: "AppDashboard",
			displayName: "App Dashboard"
		}
	},
	{
		path: '/sparql-editor',
		as: 'SPARQLEditor',
		component: SPARQLClientComponent,
		data: {
			alias: "SPARQLEditor",
			displayName: "SPARQL Editor"
		}
	}
] )
export default class CarbonAppView {
	static parameters = [ [ Router ], [ ElementRef ], [ RouteParams ], [ SidebarService ], [ AppContextService ] ];

	router:Router;
	routeParams:RouteParams;
	sidebarService:SidebarService;
	appContextService:AppContextService;

	element:ElementRef;
	$element:JQuery;
	appContext:App.Context;
	timer:number;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, sidebarService:SidebarService, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.sidebarService = sidebarService;
		this.appContextService = appContextService;

		let slug:string = this.routeParams.get( 'slug' );
		this.appContextService.get( slug ).then( ( appContext ) => {
			this.appContext = appContext;
			// this.sidebarService.addCarbonApp( this.appContext );
		} ).catch( ( error )=> {
			// TODO: Check error type
			console.log( "No Carbon App found" );
			this.timer = 5;
			let countDown = setInterval( ()=> {
				this.timer --;
				if ( this.timer == 0 ) {
					this.router.navigate( [ '/AppDev/MyApps/List' ] );
					clearInterval( countDown );
				}
			}, 1000 );
		} );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {

	}
}