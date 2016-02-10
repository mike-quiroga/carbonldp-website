import { Component, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import SideberService from "./../../components/sidebar/service/SidebarService";
import MyAppsService from "./../service/MyAppsService";
import CarbonApp from "./CarbonApp";

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'carbon-app',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ],
	providers: [ MyAppsService ]
} )
export default class CarbonAppView {
	static parameters = [ [ Router ], [ ElementRef ], [ RouteParams ], [ SideberService ], [ MyAppsService ] ];

	router:Router;
	routeParams:RouteParams;
	sideberService:SideberService;
	myAppsService:MyAppsService;

	element:ElementRef;
	$element:JQuery;
	carbonApp:CarbonApp;
	timer:number;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, sideberService:SideberService, myAppsService:MyAppsService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.sideberService = sideberService;
		this.myAppsService = myAppsService;
		try {
			let slug:string = this.routeParams.get( 'slug' );
			this.myAppsService.getapp( slug ).then(
				( carbonApp ) => {
					if ( typeof carbonApp === "undefined" ) {
						console.log( "No Carbon App found" );
						this.timer = 5;
						let countDown = setInterval( ()=> {
							this.timer --;
							if ( this.timer == 0 ) {
								this.router.navigate( [ '/AppDev/MyApps' ] );
								clearInterval( countDown );
							}
						}, 1000 );
					} else {
						this.carbonApp = carbonApp;
						this.sideberService.addItem( this.carbonApp.name );
						console.log( this.carbonApp );
					}

				},
				( error )=> {
					console.log( error );
				}
			);

		} catch ( error ) {
			console.log( error );
		}
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {

	}
}