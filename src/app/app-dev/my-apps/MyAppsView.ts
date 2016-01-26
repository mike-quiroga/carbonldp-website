import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import MyAppsService from './service/MyAppsService';
import CarbonAppThumbnailComponent from './carbon-app-thumbnail/CarbonAppThumbnailComponent';
import CarbonApp from "./carbon-app/CarbonApp";
import template from './template.html!';

@Component( {
	selector: 'my-apps',
	template: template,
	directives: [ CORE_DIRECTIVES, CarbonAppThumbnailComponent ],
	providers: [ MyAppsService ]
} )
export default class MyAppsView {
	static parameters = [ [ ElementRef ], [ MyAppsService ] ];

	router:Router;
	routeParams:RouteParams;

	element:ElementRef;
	$element:JQuery;

	myAppsService:MyAppsService;
	carbonApps:CarbonApp[] = [];

	constructor( element:ElementRef, myAppsService:MyAppsService ) {
		this.element = element;
		this.myAppsService = myAppsService;

	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.myAppsService.getApps().then(
			( apps )=> {
				apps.forEach( app=> {
					this.carbonApps.push( app );
				} );
			}
		).then(
			()=> {
				console.log( this.carbonApps );
			}
		);
	}

	onActivate():void {

	}
}